package com.ximalaya.wa.assembler;

import java.util.Map;

import com.google.common.collect.Maps;
import com.ximalaya.wa.config.Dict;
import com.ximalaya.wa.config.Keys;
import com.ximalaya.wa.helper.ValuesHolder;
import com.ximalaya.wa.model.Account;
import com.ximalaya.wa.model.Bullet;
import com.ximalaya.wa.model.Comment;
import com.ximalaya.wa.model.Donate;
import com.ximalaya.wa.model.Filter;
import com.ximalaya.wa.model.Login;
import com.ximalaya.wa.model.Payment;
import com.ximalaya.wa.model.PaymentOrder;
import com.ximalaya.wa.model.Play;
import com.ximalaya.wa.model.Recharge;
import com.ximalaya.wa.model.RelationAccount;
import com.ximalaya.wa.model.Search;
import com.ximalaya.wa.model.Share;
import com.ximalaya.wa.model.Star;
import com.ximalaya.wa.model.Subscribe;
import com.ximalaya.wa.model.UpDownload;
import com.ximalaya.wa.model.xml.Item;

public class DataAssembler {

    public static String URL_PREFIX = ValuesHolder.getValue("${url.prefix}");
    public static String FILE_PATH = ValuesHolder.getValue("${xml.store.dir}");
    public static String TEMP_PATH = ValuesHolder.getValue("${xml.temp.dir}");
    public static String ACCOUNT_PATH = ValuesHolder.getValue("${account.dir}");

    private static final Item appType = new Item();
    private static final Map<String, Item> msgType = Maps.newHashMap();
    private static final Map<String, Item> opCode = Maps.newHashMap();
    private static final Map<Class, String> queryOpCode = Maps.newHashMap();
    private static final Map<Class, String> reportOpCode = Maps.newHashMap();
    private static final Map<Class, String> monitorOpCode = Maps.newHashMap();


    static {
        // apptype
        appType.setKey(Keys.APPTYPE);
        appType.setVal(Dict.APPTYPE);
        // msgtype
        msgType.put(Dict.MSG_REQUSET, Item.Builder().key(Keys.MSGTYPE).val(Dict.MSG_REQUSET).build());
        msgType.put(Dict.MSG_RESPONSE, Item.Builder().key(Keys.MSGTYPE).val(Dict.MSG_RESPONSE).build());
        msgType.put(Dict.MSG_RESULT, Item.Builder().key(Keys.MSGTYPE).val(Dict.MSG_RESULT).build());
        
        opCode.put(Dict.OP_QUERY_NODE_STATUS, Item.Builder().key(Keys.OPCODE).val(Dict.OP_QUERY_NODE_STATUS).build());
        opCode.put(Dict.OP_COMM_CONTENT, Item.Builder().key(Keys.OPCODE).val(Dict.OP_COMM_CONTENT).build());
        
        /**
         * report opcode
         */
        opCode.put(Dict.OP_COMMENT, Item.Builder().key(Keys.OPCODE).val(Dict.OP_COMMENT).build());
        opCode.put(Dict.OP_CLOG, Item.Builder().key(Keys.OPCODE).val(Dict.OP_CLOG).build());
        opCode.put(Dict.OP_STAR, Item.Builder().key(Keys.OPCODE).val(Dict.OP_STAR).build());
        opCode.put(Dict.OP_SEARCH, Item.Builder().key(Keys.OPCODE).val(Dict.OP_SEARCH).build());
        opCode.put(Dict.OP_SHARE, Item.Builder().key(Keys.OPCODE).val(Dict.OP_SHARE).build());
        opCode.put(Dict.OP_SUBSCRIBE, Item.Builder().key(Keys.OPCODE).val(Dict.OP_SUBSCRIBE).build());
        opCode.put(Dict.OP_UPDOWNLOAD, Item.Builder().key(Keys.OPCODE).val(Dict.OP_UPDOWNLOAD).build());
        opCode.put(Dict.OP_PLAY, Item.Builder().key(Keys.OPCODE).val(Dict.OP_PLAY).build());
        opCode.put(Dict.OP_DONATE, Item.Builder().key(Keys.OPCODE).val(Dict.OP_DONATE).build());
        opCode.put(Dict.OP_LOGIN, Item.Builder().key(Keys.OPCODE).val(Dict.OP_LOGIN).build());
        opCode.put(Dict.OP_ACCOUNT, Item.Builder().key(Keys.OPCODE).val(Dict.OP_ACCOUNT).build());
        opCode.put(Dict.OP_FILTER, Item.Builder().key(Keys.OPCODE).val(Dict.OP_FILTER).build());
        opCode.put(Dict.OP_PAYMENT, Item.Builder().key(Keys.OPCODE).val(Dict.OP_PAYMENT).build());
        opCode.put(Dict.OP_PAYMENT_ORDER, Item.Builder().key(Keys.OPCODE).val(Dict.OP_PAYMENT_ORDER).build());
        opCode.put(Dict.OP_RECHARGE, Item.Builder().key(Keys.OPCODE).val(Dict.OP_RECHARGE).build());
        

        reportOpCode.put(Comment.class, Dict.OP_COMMENT);
        reportOpCode.put(Star.class, Dict.OP_STAR);
        reportOpCode.put(Search.class, Dict.OP_SEARCH);
        reportOpCode.put(Share.class, Dict.OP_SHARE);
        reportOpCode.put(Subscribe.class, Dict.OP_SUBSCRIBE);
        reportOpCode.put(UpDownload.class, Dict.OP_UPDOWNLOAD);
        reportOpCode.put(Play.class, Dict.OP_PLAY);
        reportOpCode.put(Donate.class, Dict.OP_DONATE);
        reportOpCode.put(Login.class, Dict.OP_LOGIN);
        reportOpCode.put(Account.class, Dict.OP_ACCOUNT);
        reportOpCode.put(Filter.class, Dict.OP_FILTER);
        reportOpCode.put(Payment.class, Dict.OP_PAYMENT);
        reportOpCode.put(PaymentOrder.class, Dict.OP_PAYMENT_ORDER);
        reportOpCode.put(Recharge.class, Dict.OP_RECHARGE);

        /**
         * query opcode
         */
        opCode.put(Dict.OP_QUERY_ACCOUNT, Item.Builder().key(Keys.OPCODE).val(Dict.OP_QUERY_ACCOUNT).build());
        opCode.put(Dict.OP_QUERY_COMMENT, Item.Builder().key(Keys.OPCODE).val(Dict.OP_QUERY_COMMENT).build());
        opCode.put(Dict.OP_QUERY_LOGIN, Item.Builder().key(Keys.OPCODE).val(Dict.OP_QUERY_LOGIN).build());
        opCode.put(Dict.OP_QUERY_PLAY, Item.Builder().key(Keys.OPCODE).val(Dict.OP_QUERY_PLAY).build());
        opCode.put(Dict.OP_QUERY_DONATE, Item.Builder().key(Keys.OPCODE).val(Dict.OP_QUERY_DONATE).build());
        opCode.put(Dict.OP_QUERY_RECHARGE, Item.Builder().key(Keys.OPCODE).val(Dict.OP_QUERY_RECHARGE).build());
        opCode.put(Dict.OP_QUERY_PAYMENT, Item.Builder().key(Keys.OPCODE).val(Dict.OP_QUERY_PAYMENT).build());
        opCode.put(Dict.OP_QUERY_PAYMENT_ORDER,Item.Builder().key(Keys.OPCODE).val(Dict.OP_QUERY_PAYMENT_ORDER).build());
        opCode.put(Dict.OP_QUERY_RELATED_ACCOUNT,Item.Builder().key(Keys.OPCODE).val(Dict.OP_QUERY_RELATED_ACCOUNT).build());

        queryOpCode.put(Account.class, Dict.OP_QUERY_ACCOUNT);
        queryOpCode.put(Comment.class, Dict.OP_QUERY_COMMENT);
        queryOpCode.put(Login.class, Dict.OP_QUERY_LOGIN);
        queryOpCode.put(Play.class, Dict.OP_QUERY_PLAY);
        queryOpCode.put(Donate.class, Dict.OP_QUERY_DONATE);
        queryOpCode.put(Recharge.class, Dict.OP_QUERY_RECHARGE);
        queryOpCode.put(Payment.class, Dict.OP_QUERY_PAYMENT);
        queryOpCode.put(PaymentOrder.class, Dict.OP_QUERY_PAYMENT_ORDER);
        queryOpCode.put(RelationAccount.class, Dict.OP_QUERY_RELATED_ACCOUNT);


        /**
         * monitor opcode
         */
        opCode.put(Dict.OP_MONITOR_BULLET_ADD, Item.Builder().key(Keys.OPCODE).val(Dict.OP_MONITOR_BULLET_ADD).build());
        opCode.put(Dict.OP_MONITOR_CHAT_ADD, Item.Builder().key(Keys.OPCODE).val(Dict.OP_MONITOR_CHAT_ADD).build());
        opCode.put(Dict.OP_MONITOR_PLAY_ADD, Item.Builder().key(Keys.OPCODE).val(Dict.OP_MONITOR_PLAY_ADD).build());
        opCode.put(Dict.OP_MONITOR_PAYMENT_ADD, Item.Builder().key(Keys.OPCODE).val(Dict.OP_MONITOR_PAYMENT_ADD).build());
        opCode.put(Dict.OP_MONITOR_PAYMENTORDER_ADD, Item.Builder().key(Keys.OPCODE).val(Dict.OP_MONITOR_PAYMENTORDER_ADD).build());
        opCode.put(Dict.OP_MONITOR_RECHARGE_ADD, Item.Builder().key(Keys.OPCODE).val(Dict.OP_MONITOR_RECHARGE_ADD).build());
        
        opCode.put(Dict.OP_MONITOR_BULLET_DEL, Item.Builder().key(Keys.OPCODE).val(Dict.OP_MONITOR_BULLET_DEL).build());
        opCode.put(Dict.OP_MONITOR_CHAT_DEL, Item.Builder().key(Keys.OPCODE).val(Dict.OP_MONITOR_CHAT_DEL).build());
        opCode.put(Dict.OP_MONITOR_PLAY_DEL, Item.Builder().key(Keys.OPCODE).val(Dict.OP_MONITOR_PLAY_DEL).build());
        opCode.put(Dict.OP_MONITOR_PAYMENT_DEL, Item.Builder().key(Keys.OPCODE).val(Dict.OP_MONITOR_PAYMENT_DEL).build());
        opCode.put(Dict.OP_MONITOR_PAYMENTORDER_DEL, Item.Builder().key(Keys.OPCODE).val(Dict.OP_MONITOR_PAYMENTORDER_DEL).build());
        opCode.put(Dict.OP_MONITOR_RECHARGE_DEL, Item.Builder().key(Keys.OPCODE).val(Dict.OP_MONITOR_RECHARGE_DEL).build());
        
        opCode.put(Dict.OP_MONITOR_BULLET_STATUS,Item.Builder().key(Keys.OPCODE).val(Dict.OP_MONITOR_BULLET_STATUS).build());
        opCode.put(Dict.OP_MONITOR_CHAT_STATUS, Item.Builder().key(Keys.OPCODE).val(Dict.OP_MONITOR_CHAT_STATUS).build());
        opCode.put(Dict.OP_MONITOR_PLAY_STATUS, Item.Builder().key(Keys.OPCODE).val(Dict.OP_MONITOR_PLAY_STATUS).build());
        opCode.put(Dict.OP_MONITOR_PAYMENT_STATUS, Item.Builder().key(Keys.OPCODE).val(Dict.OP_MONITOR_PAYMENT_STATUS).build());
        opCode.put(Dict.OP_MONITOR_PAYMENTORDER_STATUS, Item.Builder().key(Keys.OPCODE).val(Dict.OP_MONITOR_PAYMENTORDER_STATUS).build());
        opCode.put(Dict.OP_MONITOR_RECHARGE_STATUS, Item.Builder().key(Keys.OPCODE).val(Dict.OP_MONITOR_RECHARGE_STATUS).build());
        
        monitorOpCode.put(Bullet.class, Dict.OP_MONITOR_BULLET_ADD);
        monitorOpCode.put(Play.class, Dict.OP_MONITOR_PLAY_ADD);
        monitorOpCode.put(Payment.class, Dict.OP_MONITOR_PAYMENT_ADD);
        monitorOpCode.put(PaymentOrder.class, Dict.OP_MONITOR_PAYMENTORDER_ADD);
        monitorOpCode.put(Recharge.class, Dict.OP_MONITOR_RECHARGE_ADD);
        
        
        
        /**
         * manage opcode
         */
        opCode.put(Dict.OP_MANAGE_USERS, Item.Builder().key(Keys.OPCODE).val(Dict.OP_MANAGE_USERS).build());
        opCode.put(Dict.OP_MANAGE_AREAS, Item.Builder().key(Keys.OPCODE).val(Dict.OP_MANAGE_AREAS).build());
        opCode.put(Dict.OP_MANAGE_MESSAGES, Item.Builder().key(Keys.OPCODE).val(Dict.OP_MANAGE_MESSAGES).build());
        opCode.put(Dict.OP_MANAGE_APPFUNCTIONS, Item.Builder().key(Keys.OPCODE).val(Dict.OP_MANAGE_APPFUNCTIONS).build());


    }

    public static Item getAppType() {
        return appType;
    }
    public static String getQueryOpCode(Class type) {
        return queryOpCode.get(type);
    }
    
    public static String getReportOpCode(Class type) {
        return reportOpCode.get(type);
    }
    
    public static String getMonitorOpCode(Class type) {
        return monitorOpCode.get(type);
    }
    
    
    public static Item getQueryOpCodeItem(Class type) {
        return opCode.get(queryOpCode.get(type));
    }
    
    public static Item getReportOpCodeItem(Class type) {
        return opCode.get(reportOpCode.get(type));
    }
    
    public static Item getMonitorOpCodeItem(Class type) {
        return opCode.get(monitorOpCode.get(type));
    }
    
    public static Item getOpCode(String value) {

        return opCode.get(value);
    }

    public static Item getMsgType(String value) {
        return msgType.get(value);

    }

    public static Item getMsgId(String value) {
        Item item = new Item();
        item.setKey(Keys.MSGID);
        item.setVal(value);
        return item;
    }

    public static Item getOpId(String opId) {
        Item item = new Item();
        item.setKey(Keys.OPID);
        item.setVal(opId);
        return item;
    }

    public static Item getOpCodeItem(String opcode) {
        return opCode.get(opcode);

    }
    
    
}
