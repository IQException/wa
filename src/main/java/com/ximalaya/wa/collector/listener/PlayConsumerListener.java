package com.ximalaya.wa.collector.listener;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.ximalaya.data.event.util.NumberedKeyMapTransfer;
import com.ximalaya.wa.collector.core.DataHub;
import com.ximalaya.wa.converter.WaConverter;
import com.ximalaya.wa.model.Play;
import com.ximalaya.wa.util.MonitorUtil;

public class PlayConsumerListener extends AbstractConsumerListener {

    private static final String PLAY = "play.statistics.trace";

    @Override
    public void onReceiveMessage(String key, String message) {
        try {
            NumberedKeyMapTransfer transfer = NumberedKeyMapTransfer.instance(key);// Thread safe
            Map<String, Object> result = transfer.trans(message);
            sampleLogger.log(key+":{}",JSON.toJSONString(result));

            switch (key) {
                case PLAY:
                    Play play = WaConverter.convertToModel(Play.class, result);
                    // 布控
                     String opId = MonitorUtil.getPmId(play);
                     if(StringUtils.isNotBlank(opId)){
                    	 play.setPmId(opId);
                    	 DataHub.getPlayMonitorQueue().put(play);
                     }
                     
                    // 报送
                    DataHub.getPlayQueue().put(play);
                    break;

                default:
                    logger.debug("result is :", result);

            }

        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

}
