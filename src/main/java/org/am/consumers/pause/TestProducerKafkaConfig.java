package org.am.consumers.pause;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;

@EnableKafka
@Configuration
@PropertySource("classpath:/pause/producer.properties")
public class TestProducerKafkaConfig {

    @Autowired
    private Environment environment;

    @Bean
    public ProducerFactory<Long, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                BOOTSTRAP_SERVERS_CONFIG,
                getProperty("kafka-bootstrap-address"));
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<Long, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public TestProducer producer() {
        return new TestProducer(kafkaTemplate(), String.valueOf(getProperty("topic-name")));
    }


    private Object getProperty(String propertyName) {
        org.springframework.core.env.PropertySource propertySource = propertySourcesPlaceholderConfigurer().getAppliedPropertySources().get("environmentProperties");
        if (propertySource == null) {
            throw new RuntimeException("property source not found");
        }
        if (!propertySource.containsProperty(propertyName))
            throw new RuntimeException("property not found : " + propertyName);
        return propertySource.getProperty(propertyName);
    }
}
