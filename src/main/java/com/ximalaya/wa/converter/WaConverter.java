package com.ximalaya.wa.converter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import com.clearspring.analytics.util.Lists;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Multimaps;
import com.ximalaya.stat.count.client.thrift.ThriftSimpleCountCollecterClient;
import com.ximalaya.wa.annotation.Mapped;
import com.ximalaya.wa.assembler.DataAssembler;
import com.ximalaya.wa.collector.core.WaContext;
import com.ximalaya.wa.config.BizType;
import com.ximalaya.wa.config.Dict;
import com.ximalaya.wa.helper.BeansHolder;
import com.ximalaya.wa.model.Account;
import com.ximalaya.wa.model.CommonProcessor;
import com.ximalaya.wa.model.xml.CommonData;
import com.ximalaya.wa.model.xml.CommonResponse;
import com.ximalaya.wa.model.xml.Data;
import com.ximalaya.wa.model.xml.Item;
import com.ximalaya.wa.model.xml.ManageResult;
import com.ximalaya.wa.model.xml.MonitorResult;
import com.ximalaya.wa.model.xml.QueryResponse;
import com.ximalaya.wa.model.xml.ReportResult;
import com.ximalaya.wa.model.xml.ResponseInfo;
import com.ximalaya.wa.model.xml.ResultInfo;
import com.ximalaya.wa.util.MapUtil;

import avro.shaded.com.google.common.collect.Maps;

@SuppressWarnings("all")
public class WaConverter {

    private static final Logger LOG = LoggerFactory.getLogger(WaConverter.class);
    private static final String METHOD_GET_OPID = "getOpId";
    private static final String METHOD_GET_PMID = "getPmId";
    private static final String METHOD_GET_RECUID = "getRecUid";
    private static final String METHOD_GET_UID = "getUid";
    private static final String METHOD_GET_AREACODE = "getAreacode";
    private static final String METHOD_GET_TRACKID = "getTrackId";
    private static final String METHOD_GET_ACTIONTIME = "getActionTime";

    private static final String AREACODE = "areacode";

    // just for 全量注册上传
    public static List<Account> convertToAccountList(int batchSize, List<Map<String, Map<String, Object>>> result)
        throws Exception {

        if (CollectionUtils.isEmpty(result))
            return Collections.EMPTY_LIST;
        ThriftSimpleCountCollecterClient statCount = BeansHolder.getBean(ThriftSimpleCountCollecterClient.class);
        List<Account> beans = Lists.newArrayList();
        for (Map map : result) {
            beans.add(convertToModel(Account.class, map));
        }
        List<Account> filteredBeans = Lists.newArrayList();
        filteredBeans.addAll(Collections2.filter(beans, new Predicate<Account>() {

            @Override
            public boolean apply(Account bean) {

                if (StringUtils.isBlank(bean.getUid()) || "0".equals(beans)) {
                    return false;
                }
                return true;
            }
        }));
        int fromIndex = 0;
        int toIndex = fromIndex + batchSize;
        do {
            if (toIndex > filteredBeans.size())
                toIndex = filteredBeans.size();
            List<Account> sublist = filteredBeans.subList(fromIndex, toIndex);
            String[] uidsArr = new String[sublist.size()];

            for (int i = 0; i < sublist.size(); i++) {
                uidsArr[i] = String.valueOf(sublist.get(i));
            }

            long[] followingsCounts = statCount.getByIds("user.followings.count", uidsArr);
            long[] followersCounts = statCount.getByIds("user.followers.count", uidsArr);
            for (int i = 0; i < sublist.size(); i++) {
                Account account = sublist.get(i);
                account.setFollowerNum(String.valueOf(followersCounts[i]));
                account.setFollowNum(String.valueOf(followingsCounts[i]));
            }
            fromIndex = toIndex;
            toIndex = fromIndex + batchSize;

        } while (fromIndex < filteredBeans.size());

        return filteredBeans;
    }

    /**
     * 批量转换
     * 
     * @param clazz
     * @param result
     * @return
     * @throws Exception
     */
    public static <T extends CommonProcessor> List<T> convertToModelList(Class<T> clazz,
        List<Map<String, Map<String, Object>>> result) throws Exception {
        if (CollectionUtils.isEmpty(result))
            return Collections.EMPTY_LIST;
        List<T> beans = Lists.newArrayList();
        for (Map map : result) {
            beans.add(convertToModel(clazz, map));
        }
        return beans;
    }

    /**
     * 将从ES或kafka接到的map数据转换成model
     * 
     * @param clazz
     * @param result
     * @return
     * @throws Exception
     */
    public static <T extends CommonProcessor> T convertToModel(Class<T> clazz, Map result) throws Exception {
        Map<Field, Mapped> fieldInfo = WaContext.getAnnotatedFields(clazz);
        CommonProcessor instance = clazz.newInstance();
        for (Map.Entry<Field, Mapped> annField : fieldInfo.entrySet()) {
            if (StringUtils.isBlank(annField.getValue().xm()))
                continue;
            Object value = null;

            if(annField.getKey().getName().equals(AREACODE)){
                value = MapUtil.getFirstNotNullAndNotEquals(result, annField.getValue().xm(),"000000","430000");
            }else{
                value = MapUtil.getFirstNotNull(result, annField.getValue().xm()) ;
            }
            if (value == null) {          
                continue;
            }
            annField.getKey().set(instance, String.valueOf(value));
        }
        instance.fixInfo();
        return (T) instance;
    }

    /**
     * 将从ES或kafka接到的map数据转换成model（需要手动设置某些属性的，先初始化bean，设置完属性调用这个方法）
     * 
     * @param clazz
     * @param result
     * @return
     * @throws Exception
     */
    public static <T extends CommonProcessor> void convertToModel(T bean, Map result) throws Exception {
        Class<? extends CommonProcessor> clazz = bean.getClass();
        Map<Field, Mapped> fieldInfo = WaContext.getAnnotatedFields(clazz);
        for (Map.Entry<Field, Mapped> annField : fieldInfo.entrySet()) {
            if (StringUtils.isBlank(annField.getValue().xm()))
                continue;
            Object value = null;
            if(annField.getKey().getName().equals(AREACODE)){
                value = MapUtil.getFirstNotNullAndNotEquals(result, annField.getValue().xm(),"000000","430000");
            }else{
                value = MapUtil.getFirstNotNull(result, annField.getValue().xm());
            }

            if (value == null)
                continue;
            annField.getKey().set(bean, String.valueOf(value));
        }
        bean.fixInfo();
    }

    /**
     * 布控结果（xml model）：以opid分组
     * 
     * @param beans
     * @return
     * @throws Exception
     */
    public static List<ResultInfo<MonitorResult>> convertToMonitorResult(List beans) throws Exception {

        if (CollectionUtils.isEmpty(beans))
            return Collections.EMPTY_LIST;
        List<ResultInfo<MonitorResult>> results = Lists.newArrayList();
        Class clazz = beans.get(0).getClass();
        Map<String, List<Data>> datasMap = getMonitorDataSet(beans);
        for (Map.Entry<String, List<Data>> datas : datasMap.entrySet()) {

        	String[] pmId = datas.getKey().split("_");
        	if(CollectionUtils.isNotEmpty(datas.getValue())){
                CommonData commonResult = getCommonData(clazz, BizType.MONITOR, Dict.MSG_RESULT, pmId[0],pmId[1]);
//                ReportResult monitorResult = new ReportResult(commonResult.getCommonDatas(), datas.getValue());
                
                MonitorResult monitorResult = new MonitorResult(commonResult.getCommonDatas(), datas.getValue());
                ResultInfo<MonitorResult> result = new ResultInfo<>();
                result.setResult(monitorResult);
                results.add(result);
        	}

        }

        return results;

    }

    /**
     * 布控结果（beans）：以opid分组
     * 
     * @param beans
     * @return
     * @throws Exception
     */
    public static Map<String, List<Data>> getMonitorDataSet(List beans) throws Exception {

        Map<String, List<Data>> datasMap = Maps.newHashMap();
        Map<String, Collection> beansMap = Maps.newHashMap();

        if (CollectionUtils.isEmpty(beans))
            return datasMap;
        Class clazz = beans.get(0).getClass();
        Function<Object, String> function = new Function<Object, String>() {
            public String apply(Object bean) {
                try {
                    Method getOpId = clazz.getMethod(METHOD_GET_PMID);
                    String opId = (String) getOpId.invoke(bean);
                    return opId;

                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                    return StringUtils.EMPTY;
                }
            }
        };

        beansMap = Multimaps.index(beans, function).asMap();

        for (Map.Entry<String, Collection> beansEntry : beansMap.entrySet()) {
            List list = new ArrayList<>();
            list.addAll(beansEntry.getValue());
            datasMap.put(beansEntry.getKey(), getDataSet(list));
        }

        return datasMap;
    }

    /**
     * 将model list 转换成xml model
     * 
     * @param beans
     * @return
     * @throws Exception
     */
    public static <T> ResultInfo<T> convertToResult(BizType bizType, List beans, String opid) throws Exception {
        ResultInfo resultInfo = new ResultInfo();
        if (CollectionUtils.isEmpty(beans))
            return resultInfo;
        Class clazz = beans.get(0).getClass();
        CommonData commonResult = getCommonData(clazz, bizType, Dict.MSG_RESULT, opid, null);
        List<Data> datas = getDataSet(beans);
        if (CollectionUtils.isEmpty(datas))
            return resultInfo;       
        T result = null;
        switch (bizType) {
            case REPORT:
                result = (T) new ReportResult(commonResult.getCommonDatas(), datas);
                break;
            case MONITOR:
                result = (T) new ManageResult(commonResult.getCommonDatas(), datas);
                break;
            default:
                LOG.error("unsupported bizType :"+ bizType.name());
        }
        resultInfo.setFileName(commonResult.getFileName());
        resultInfo.setRecordNum(datas.size());
        resultInfo.setResult(result);
        return resultInfo;
    }

    /**
     * beans转成 xml model 的datas属性
     * 
     * @param beans
     * @return
     * @throws Exception
     */
    public static List<Data> getDataSet(List beans) throws Exception {
        List<Data> datas = new ArrayList<Data>();
        if (CollectionUtils.isEmpty(beans))
            return datas;

        Class clazz = beans.get(0).getClass();
        int filterNumNoUid = 0;
        int filterNumNoAreaCode = 0;
        int filterNumNoActionTime = 0;
        int filterNumIllegalAreaCode = 0;
        int filterNumNoTrackId = 0;

        for (Object bean : beans) {
            Map<Field, Mapped> fieldInfo = WaContext.getAnnotatedFields(clazz);
            try {
                Method getUid = ReflectionUtils.findMethod(clazz, METHOD_GET_UID);
                String uid = (String) getUid.invoke(bean);
                if (StringUtils.isBlank(uid) || "0".equals(uid)){
                  ++filterNumNoUid;
                  continue;
                }
                Method getAreaCode = ReflectionUtils.findMethod(clazz, METHOD_GET_AREACODE);
                String areaCode = (String) getAreaCode.invoke(bean);
                if (areaCode==null){
                	++filterNumNoAreaCode;
                    continue;
                }else if("000000".equals(areaCode)){
                    ++filterNumIllegalAreaCode;
                    continue;
                }                
                Method getActionTime = ReflectionUtils.findMethod(clazz,METHOD_GET_ACTIONTIME);
                String actionTime = (String) getActionTime.invoke(bean);
                if (StringUtils.isBlank(actionTime)){
                	++filterNumNoActionTime;
                    continue;
                }
                Method getTrackId = ReflectionUtils.findMethod(clazz,METHOD_GET_TRACKID);
                String trackId = (String) getTrackId.invoke(bean);
                if(StringUtils.isBlank(trackId) || "0".equals(trackId)) {
                	filterNumNoTrackId++;
                	  continue;
                }
                
            } catch (Exception e) {
//                LOG.warn(e.getMessage(),e);
            }

            List<Item> items = new ArrayList<Item>();
            for (Map.Entry<Field, Mapped> annField : fieldInfo.entrySet()) {

                Object value = annField.getKey().get(bean);
                if (value == null)
                    continue;
                String valueStr = (String) value;
                value = escapeBlank(valueStr);
                Item item = new Item();
                item.setKey(annField.getValue().wa());
                if (hasSpecialChar(valueStr) || annField.getValue().enc()) {
                    valueStr = encode(valueStr);
                    item.setFmt("base64");
                }
                item.setVal(valueStr);
                items.add(item);

            }
            Data data = new Data();
            data.setItems(items);
            datas.add(data);
        }
		LOG.info(clazz.getName() + ":     total-" + beans.size()+"    filterNumNoUid-" + filterNumNoUid + "      filterNumNoAreaCode-"
				+ filterNumNoAreaCode  +"    filterNumIllegalAreaCode-"+ filterNumIllegalAreaCode + "     filterNumNoActionTime-" + filterNumNoActionTime
			    + "     filterNumNoTrackId-"+filterNumNoTrackId);
        return datas;
    }
    
    public static List<Data> getDataSet(List beans,boolean isNeedFilted) throws Exception {
        List<Data> datas = new ArrayList<Data>();
        if (CollectionUtils.isEmpty(beans))
            return datas;

        Class clazz = beans.get(0).getClass();
        
        int filterNumNoUid = 0;
        int filterNumNoAreaCode = 0;
        int filterNumNoActionTime = 0;
        int filterNumIllegalAreaCode = 0;
        int filterNumNoTrackId = 0;

        for (Object bean : beans) {
            Map<Field, Mapped> fieldInfo = WaContext.getAnnotatedFields(clazz);
            
            if (isNeedFilted) {
            	try {
                    Method getUid = ReflectionUtils.findMethod(clazz, METHOD_GET_UID);
                    String uid = (String) getUid.invoke(bean);
                    if (StringUtils.isBlank(uid) || "0".equals(uid)){
                      ++filterNumNoUid;
                      continue;
                    }
                    Method getAreaCode = ReflectionUtils.findMethod(clazz, METHOD_GET_AREACODE);
                    String areaCode = (String) getAreaCode.invoke(bean);
                    if (areaCode==null){
                    	++filterNumNoAreaCode;
                        continue;
                    }else if("000000".equals(areaCode)){
                        ++filterNumIllegalAreaCode;
                        continue;
                    }                
                    Method getActionTime = ReflectionUtils.findMethod(clazz,METHOD_GET_ACTIONTIME);
                    String actionTime = (String) getActionTime.invoke(bean);
                    if (StringUtils.isBlank(actionTime)){
                    	++filterNumNoActionTime;
                        continue;
                    }
                    Method getTrackId = ReflectionUtils.findMethod(clazz,METHOD_GET_TRACKID);
                    String trackId = (String) getTrackId.invoke(bean);
                    if(StringUtils.isBlank(trackId)) {
                    	filterNumNoTrackId++;
                    	  continue;
                    }
                    
                } catch (Exception e) {
//                    LOG.warn(e.getMessage(),e);
                }
			}

            List<Item> items = new ArrayList<Item>();
            for (Map.Entry<Field, Mapped> annField : fieldInfo.entrySet()) {

                Object value = annField.getKey().get(bean);
                if (value == null)
                    continue;
                String valueStr = (String) value;
                value = escapeBlank(valueStr);
                Item item = new Item();
                item.setKey(annField.getValue().wa());
                if (hasSpecialChar(valueStr) || annField.getValue().enc()) {
                    valueStr = encode(valueStr);
                    item.setFmt("base64");
                }
                item.setVal(valueStr);
                items.add(item);

            }
            Data data = new Data();
            data.setItems(items);
            datas.add(data);
        }
		LOG.info(clazz.getName() + ":     total-" + beans.size()+"    filterNumNoUid-" + filterNumNoUid + "      filterNumNoAreaCode-"
				+ filterNumNoAreaCode  +"    filterNumIllegalAreaCode-"+ filterNumIllegalAreaCode + "     filterNumNoActionTime-" + filterNumNoActionTime
			    + "     filterNumNoTrackId-"+filterNumNoTrackId);
        return datas;
    }
    
    public static CommonData getCommonData(String opCode, String msgtype,  String opid) throws Exception {
    	return getCommonData(opCode, msgtype, null,  opid);
    }
    /**
     * 组装xml model 的commonData部分
     * 
     * @param opCode
     * @param msgtype
     * @param opid
     * @return
     * @throws Exception
     */

    public static CommonData getCommonData(String opCode, String msgtype, String msgId, String opid) throws Exception {

        List<Data> datas = new ArrayList<Data>(1);
        Data data = new Data();
        List<Item> items = new ArrayList<Item>();
        Item appType = DataAssembler.getAppType();
        Item msgType = DataAssembler.getMsgType(msgtype);
       
        Item msgid = null;
        if (msgId == null) {
        	msgid = DataAssembler.getMsgId(genSeq());
		}else{
			msgid = DataAssembler.getMsgId(msgId);
		}
        
        Item opcode = DataAssembler.getOpCodeItem(opCode);

        items.add(appType);
        items.add(opcode);
        items.add(msgid);
        items.add(msgType);
        
        if (opid != null) {
            Item opIdItem = DataAssembler.getOpId(opid);
            items.add(opIdItem);
        }
        
        data.setItems(items);
        datas.add(data);

        String filePath = Joiner.on('/').join(new String[] { appType.getVal(), opcode.getVal(), msgType.getVal() }) + "/";
        String fileName = Joiner.on('_').join(new String[] { appType.getVal(), opcode.getVal(), msgType.getVal(),
        		msgid.getVal(), String.format("%04d", (int) (1 + Math.random() * 9998)), "V2.xml" });

        CommonData result = new CommonData();
        result.setCommonDatas(datas);
        result.setFilePath(filePath);
        result.setFileName(fileName);
        return result;

    }

    /**
     * adapter：以clazz替代opcode
     * 
     * @param clazz
     * @param biztype
     * @param msgtype
     * @param opid
     * @return
     * @throws Exception
     */
    public static CommonData getCommonData(Class clazz, BizType biztype, String msgtype, String opid,String msgId) throws Exception {
        String opcode =null;
        switch (biztype) {
            case REPORT:
                opcode = DataAssembler.getReportOpCode(clazz);
                break;
            case QUERY:
                opcode = DataAssembler.getQueryOpCode(clazz);
                break;
            case MONITOR:
                opcode = DataAssembler.getMonitorOpCode(clazz);
                break;
            default:
             LOG.error("unsupported bizType :"+ biztype.name());
        }
        return getCommonData(opcode, msgtype, msgId, opid);
    }

    /**
     * 组装响应 xml model
     * 
     * @param status
     * @param opCode
     * @param opId
     * @return
     * @throws Exception
     */
    public static ResponseInfo<CommonResponse> convertToCommonResponse(List<Data> status,List<Data> monitorResult, String opCode, String msgId, String opId)
        throws Exception {

        Preconditions.checkArgument(CollectionUtils.isNotEmpty(status));

        CommonData commonResult = getCommonData(opCode, Dict.MSG_RESPONSE, msgId, opId);
        CommonResponse monitorResponse = new CommonResponse(commonResult.getCommonDatas(), status, monitorResult);
        ResponseInfo<CommonResponse> responseInfo = new ResponseInfo();
        responseInfo.setResponse(monitorResponse);

        return responseInfo;
    }

    public static ResponseInfo<QueryResponse> convertToQueryResponse(List<Data> status, List beans,String msgId) throws Exception {
    	ResponseInfo<QueryResponse> response = new ResponseInfo();
        if (CollectionUtils.isEmpty(beans))
            return response;
        Class clazz = beans.get(0).getClass();
        CommonData commonResult = getCommonData(clazz, BizType.QUERY, Dict.MSG_RESPONSE, null, msgId);
        List<Data> datas = getDataSet(beans,false);
        if (CollectionUtils.isEmpty(datas))
            return response;
        QueryResponse queryResponse = new QueryResponse(commonResult.getCommonDatas(), status, datas);
        response.setResponse(queryResponse);

        return response;
    }

    public static <T> ResponseInfo<T> convertToResponse(BizType bizType, String opCode, List<Data> status)
        throws Exception {

        CommonData commonData = getCommonData(opCode, Dict.MSG_RESPONSE, null);
        T response = null;
        switch (bizType) {
            case QUERY:
                response = (T) new QueryResponse(commonData.getCommonDatas(), status);
                break;
            default:
                LOG.error("unsupported bizType :"+ bizType.name());
        }
        ResponseInfo<T> responseInfo = new ResponseInfo();
        responseInfo.setResponse(response);
        return responseInfo;
    }

    private static String encode(String value) {
        return new String(Base64.encodeBase64(value.getBytes()));
    }

    private static String escapeBlank(String value) {

        return value.replaceAll("\\t|\\n", "");
    }

    public static String escapePunct(String value) {
        return value.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
    }

    private static String genSeq() {

        return Dict.AREA_SH + System.currentTimeMillis();
    }

    private static boolean hasSpecialChar(String value) {
        return value.indexOf('<') != -1 || value.indexOf('>') != -1 || value.indexOf('/') != -1
            || value.indexOf('\'') != -1 || value.indexOf('"') != -1 || value.indexOf('&') != -1;
    }

    /**
     * IP转成long
     * 
     * @param ip
     * @return
     */
    public static String ip2Long(String ip) {
        if (StringUtils.isEmpty(ip))
            return null;

        ip = ip.replaceAll("^0-9\\.", ""); // 去除字符串前的空字符
        String[] ips = ip.split("\\.");
        if (ips.length == 4) {
            Long num = Long.parseLong(ips[0]) * 256 * 256 * 256 + Long.parseLong(ips[1]) * 256 * 256
                + Long.parseLong(ips[2]) * 256 + Long.parseLong(ips[3]);
            ip = num.toString();
        }

        return ip;
    }


    /**
     * long转IP
     * 
     * @param ip
     * @return
     */
    public static String long2Ip(long longIp) {

        StringBuffer sb = new StringBuffer("");  
        // 直接右移24位  
        sb.append(String.valueOf((longIp >>> 24)));  
        sb.append(".");  
        // 将高8位置0，然后右移16位  
        sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));  
        sb.append(".");  
        // 将高16位置0，然后右移8位  
        sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));  
        sb.append(".");  
        // 将高24位置0  
        sb.append(String.valueOf((longIp & 0x000000FF)));  
        return sb.toString();  
    }
    
    public static void main(String[] args) throws Exception {
        System.err.println(String.valueOf(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+08:00'").parse("2018-03-01T10:46:24+08:00").getTime()).substring(0,10));
        
        String url = "CIjgtIn7mewCEL2Qztb6mewCGKXlj58rIKXlj58rKK7gg8Xch-wCMA44AUIgMjAxODA2MDUyMTU3MjMwMTAwMDgwNjAxMzc5OTZFQ0ZIAVAA";
        String account = "MTUyNjcxODc2MDU=";
        System.out.println(new String(Base64.decodeBase64(URLDecoder.decode(url,"UTF-8"))));
        System.out.println(new String(Base64.decodeBase64(account)));
        
    }
}
