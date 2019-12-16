package org.am.delay;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.PropertySource;

public class PausingConsumerApplication {

    public static void main(String[] s) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ConsumerConfig.class);
        PausingConsumer pausingConsumer = applicationContext.getBean(PausingConsumer.class);
        PropertySource propertySource = applicationContext.getBean(PropertySource.class);

        String topicName = String.valueOf(propertySource.getProperty("topic-name"));
        pausingConsumer.consume(new TopicPartition(topicName, 0));
    }
}

