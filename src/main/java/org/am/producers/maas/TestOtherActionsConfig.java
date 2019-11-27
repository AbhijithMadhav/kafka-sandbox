package org.am.producers.maas;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;


/**
 * App Context config for test producer
 */
@Configuration
@EnableKafka
public class TestOtherActionsConfig {


    private final static Logger LOGGER = LoggerFactory.getLogger(TestOtherActionsConfig.class);

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;


    @Bean
    public ProducerFactory<Long, AppsEventModel> producerFactory() {

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class
        );
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class
        );
        // custom partitioner
        configProps.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, DeviceIdPartitioner.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<Long, AppsEventModel> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }


    @Bean
    public TestOtherActionsProducer producer() {
        return new TestOtherActionsProducer(kafkaTemplate());
    }
}
