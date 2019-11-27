package org.am.consumers.pause;

import org.am.producers.test.AppEvent;
import org.am.producers.test.AppEventPartitioner;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
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

@EnableKafka
@Configuration
public class TestProducerKafkaConfig {
    //@Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress = "127.0.0.1:9092";

    @Bean
    public ProducerFactory<Long, Long> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        //configProps.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, AppEventPartitioner.class);
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<Long, Long> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }


    @Bean
    public TestProducer producer() {
        return new TestProducer();
    }
}
