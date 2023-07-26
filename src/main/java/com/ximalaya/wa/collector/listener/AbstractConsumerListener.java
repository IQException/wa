package com.ximalaya.wa.collector.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ximalaya.kafka.consumer.ConsumerListener;
import com.ximalaya.mobile.common.SampleLogger;

public abstract class AbstractConsumerListener implements ConsumerListener {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    protected final SampleLogger sampleLogger = SampleLogger.getLogger(getClass(),300);

    @Override
    public void onReceiveMessage(String message) {
    }
}
