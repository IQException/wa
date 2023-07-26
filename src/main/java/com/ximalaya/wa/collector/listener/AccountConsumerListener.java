package com.ximalaya.wa.collector.listener;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.ximalaya.data.event.util.NumberedKeyMapTransfer;
import com.ximalaya.wa.collector.core.DataHub;
import com.ximalaya.wa.config.Dict;
import com.ximalaya.wa.converter.WaConverter;
import com.ximalaya.wa.model.Account;
import com.ximalaya.wa.model.Login;

public class AccountConsumerListener extends AbstractConsumerListener {
    private static final String LOGIN = "login";
    private static final String REGISTER = "register";
    private static final String UPDATE = "profile-update";
    private static final String RELATION_ACCOUNT = "third_account_relation";

    @Override
    public void onReceiveMessage(String key, String message) {
        try {
            NumberedKeyMapTransfer transfer = NumberedKeyMapTransfer.instance(key);// Thread safe
            Map<String,Object> result = transfer.trans(message);
            sampleLogger.log(key+":{}",JSON.toJSONString(result));

            switch (key) {
                case LOGIN:
                    Login login = WaConverter.convertToModel(Login.class, result);
                    DataHub.getLoginQueue().put(login);
                    break;

                case UPDATE:
                    Account update = new Account();
                    update.setActionType(Dict.ACTION_REG_MOD);
                    WaConverter.convertToModel(update, result);
                    DataHub.getAccountQueue().put(update);
                    break;

                case REGISTER:
                    Account register = new Account();
                    register.setActionType(Dict.ACTION_REG_ADD);
                    WaConverter.convertToModel(register, result);
                    DataHub.getAccountQueue().put(register);                    
                    break;

                // TODO actionType
                // case RELATION_ACCOUNT :
                // RelationAccount relationAccount =
                // WaConverter.convertToModel(RelationAccount.class, result);
                // relationAccount.setActionType(Dict.ACTION_REG_MOD);
                // DataHub.getRelationAccountQueue().put(relationAccount);
                // break;
                default:
                    logger.debug("result is :", result);
            }

        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

}
