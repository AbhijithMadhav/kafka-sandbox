package org.am.consumers.pause;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class TestProducerApplication {

    private final static Logger LOGGER = LoggerFactory.getLogger(TestProducerApplication.class);

    public static final String TOPIC = "multi-partition-test-topic";

    public static void main(String s[]) throws InterruptedException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(TestProducerKafkaConfig.class);

        TestProducer testProducer = applicationContext.getBean(TestProducer.class);

        Random r = new Random();
        IntStream.range(0, 5).forEach(i -> testProducer.sendMessage(
                Instant.now().plusSeconds(15).toEpochMilli(),
                TOPIC)
        );

        LOGGER.info("Sleeping for 20 secs to wait for the broker to ack the sends...");
        // sleep for 5 seconds to wait for kafka to acknowledge sends
        Thread.sleep(TimeUnit.SECONDS.toMillis(15));
        LOGGER.info("Done Producing!");
    }

}
