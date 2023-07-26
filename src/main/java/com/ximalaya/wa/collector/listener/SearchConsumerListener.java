package com.ximalaya.wa.collector.listener;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.ximalaya.data.event.util.NumberedKeyMapTransfer;
import com.ximalaya.wa.collector.core.DataHub;
import com.ximalaya.wa.converter.WaConverter;
import com.ximalaya.wa.model.Search;

public class SearchConsumerListener extends AbstractConsumerListener {

    private static final String SEARCH = "search";

    @Override
    public void onReceiveMessage(String key, String message) {
        try {
            NumberedKeyMapTransfer transfer = NumberedKeyMapTransfer.instance(key);// Thread safe
            Map<String, Object> result = transfer.trans(message);
            sampleLogger.log(key+":{}",JSON.toJSONString(result));

            switch (key) {
                case SEARCH:
                    Search search = WaConverter.convertToModel(Search.class, result);
                    DataHub.getSearchQueue().put(search);
                    break;

                default:
                    logger.debug("result is :", result);

            }

        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

}
