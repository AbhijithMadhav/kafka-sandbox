package org.am.consumers.pause;

import org.am.producers.test.AppEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.Instant;
import java.util.Random;

public class TestProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestProducer.class);

    private static final Random random = new Random();

    @Autowired
    private KafkaTemplate<Long, Long> kafkaTemplate;

    public void sendMessage(final Long validAfterEpochMilli, final String topicName) {


        ListenableFuture<SendResult<Long, Long>> future = kafkaTemplate.send(topicName, (long)random.nextInt(2), validAfterEpochMilli);
        LOGGER.info("Sending message to {} : {}", topicName,  validAfterEpochMilli);

        // Asynchronous so that producer is not slowed down
        future.addCallback(new ListenableFutureCallback<SendResult<Long, Long>>() {

            @Override
            public void onSuccess(SendResult<Long, Long> result) {
                LOGGER.info("Sent message : {}", result);
            }

            @Override
            public void onFailure(Throwable ex) {
                LOGGER.warn("Unable to send message=["
                        + validAfterEpochMilli + "] due to : " + ex.getMessage());
            }
        });
    }
}