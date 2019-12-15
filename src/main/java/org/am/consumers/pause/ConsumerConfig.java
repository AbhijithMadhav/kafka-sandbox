package org.am.consumers.pause;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.kafka.annotation.EnableKafka;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ISOLATION_LEVEL_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

@EnableKafka
@PropertySource(value = {"classpath:/pause/consumer.properties"})
@Configuration
class ConsumerConfig {

    @Bean
    public PausingConsumer pausingConsumer() {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, String.valueOf(getProperty("kafka-bootstrap-servers")));
        props.put(GROUP_ID_CONFIG, "pausing-consumers");
        props.put(ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ISOLATION_LEVEL_CONFIG, "read_committed");
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.LongDeserializer");
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer"); // TODO : custom serializer needed for custom message
        KafkaConsumer<Long, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(String.valueOf(getProperty("topic-name"))));
        return new PausingConsumer(consumer, Duration.ofSeconds(Long.valueOf(String.valueOf(getProperty("valid-after-duration-in-secs")))), Duration.ofMillis(Long.valueOf(String.valueOf(getProperty("polling-interval-in-millis")))));
    }

    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        return propertySourcesPlaceholderConfigurer;
    }

    private Object getProperty(String propertyName) {
        org.springframework.core.env.PropertySource propertySource = propertySourcesPlaceholderConfigurer().getAppliedPropertySources().get("environmentProperties");
        if (propertySource == null) {
            throw new RuntimeException("property source not found");
        }
        if (!propertySource.containsProperty(propertyName))
            throw new RuntimeException("property not found : " + propertyName);
        System.out.println(propertySource.getProperty(propertyName));
        return propertySource.getProperty(propertyName);
    }

}
