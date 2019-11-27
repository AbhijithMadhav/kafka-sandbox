package org.am.consumers.slow_consumer;

import org.am.producers.test.AppEvent;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG;


@EnableKafka
@Configuration
public class TestConsumerKafkaConfig {

    // @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress = "127.0.0.1:9092";


    // @Value()
    private Integer nConsumers = 1;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, AppEvent> kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<Long, AppEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(nConsumers);

        // commit strategy
        factory.getContainerProperties().setAckMode(AbstractMessageListenerContainer.AckMode.RECORD);
        return factory;
    }

    @Bean
    public ConsumerFactory<Long, AppEvent> consumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        // Put the consumers in this group
        props.put(GROUP_ID_CONFIG, "autocommit-consumers");
        props.put(ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(MAX_POLL_INTERVAL_MS_CONFIG, 5000);

        return new DefaultKafkaConsumerFactory<>(props, new LongDeserializer(), new JsonDeserializer(AppEvent.class));
    }

    @Bean
    TestConsumer consumer() {
        return new TestConsumer();
    }

    @Bean
    KafkaListenerErrorHandler testConsumerErrorhandler() {
        return new TestConsumerErrorhandler();
    }
}