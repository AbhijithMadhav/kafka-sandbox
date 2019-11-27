package org.am.consumers.slow_consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;

public class TestConsumerErrorhandler implements KafkaListenerErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestConsumerErrorhandler.class);

    @Override
    public Object handleError(Message<?> message, ListenerExecutionFailedException exception) throws Exception {

        LOGGER.warn("Error processing message : {}", message, exception);
        return null;
    }
}
