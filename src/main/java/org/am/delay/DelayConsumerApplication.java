package org.am.delay;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.PropertySource;

public class DelayConsumerApplication {

    public static void main(String[] s) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ConsumerConfig.class);
        DelayConsumer delayConsumer = applicationContext.getBean(DelayConsumer.class);
        PropertySource propertySource = applicationContext.getBean(PropertySource.class);

        String topicName = String.valueOf(propertySource.getProperty("topic-name"));
        delayConsumer.consume(new TopicPartition(topicName, 0));
    }
}

