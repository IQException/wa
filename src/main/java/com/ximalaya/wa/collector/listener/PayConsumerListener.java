package com.ximalaya.wa.collector.listener;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.ximalaya.data.event.util.NumberedKeyMapTransfer;
import com.ximalaya.wa.collector.core.DataHub;
import com.ximalaya.wa.converter.WaConverter;
import com.ximalaya.wa.model.Donate;

public class PayConsumerListener extends AbstractConsumerListener {

    private static final String GIFT = "gift";

    @Override
    public void onReceiveMessage(String key, String message) {
        try {
            NumberedKeyMapTransfer transfer = NumberedKeyMapTransfer.instance(key);// Thread safe
            Map<String, Object> result = transfer.trans(message);
            sampleLogger.log(key+":{}",JSON.toJSONString(result));

            switch (key) {
                case GIFT:
                    Donate donate = WaConverter.convertToModel(Donate.class, result);
                    DataHub.getDonateQueue().put(donate);
                    break;

                default:
                    logger.debug("result is :", result);
            }

        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

}
