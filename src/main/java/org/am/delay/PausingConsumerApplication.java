package org.am.delay;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class PausingConsumerApplication {

    public static void main(String s[]) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ConsumerConfig.class);
        PausingConsumer pausingConsumer = applicationContext.getBean(PausingConsumer.class);
        pausingConsumer.consume();
    }
}

