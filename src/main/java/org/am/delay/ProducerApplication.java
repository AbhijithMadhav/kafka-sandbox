package org.am.delay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class ProducerApplication {

    private final static Logger LOGGER = LoggerFactory.getLogger(ProducerApplication.class);

    public static void main(String s[]) throws InterruptedException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ProdConfig.class);

        Producer producer = applicationContext.getBean(Producer.class);

        final Random random = new Random();
        final String message = "message-" + (long) random.nextInt();
        IntStream.range(0, 500000).forEach(i -> producer.sendMessage(message));

        LOGGER.info("Sleeping for 20 secs to wait for the broker to ack the sends...");
        // sleep for 5 seconds to wait for kafka to acknowledge sends
        Thread.sleep(TimeUnit.SECONDS.toMillis(20));
        LOGGER.info("Done Producing!");
    }
}
