package com.ximalaya.wa.collector.listener;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import com.alibaba.fastjson.JSON;
import com.ximalaya.mobile.common.SampleLogger;
import com.ximalaya.wa.collector.core.DataHub;
import com.ximalaya.wa.converter.WaConverter;
import com.ximalaya.wa.model.Filter;

public class TrackDestroyedListener implements MessageListener {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());
    protected final SampleLogger sampleLogger = SampleLogger.getLogger(getClass(),300);

    @Override
    public void onMessage(Message message) {
        try {
            Map<String, Object> result = JSON.parseObject(new String(message.getBody()));
            sampleLogger.log(JSON.toJSONString(result));
            Filter filter = WaConverter.convertToModel(Filter.class, result);
            DataHub.getFilterQueue().put(filter);
        } catch (Throwable e) {
            LOG.error(e.getMessage(), e);
        }
    }

}
