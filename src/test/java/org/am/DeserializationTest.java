package org.am;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.am.producers.test.AppEvent;
import org.apache.kafka.common.serialization.Deserializer;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DeserializationTest {

    @Test
    public void test() throws IOException {
        List<AppEvent> appEvents = new LinkedList<>();
        appEvents.add(new AppEvent(1, "name1"));
        appEvents.add(new AppEvent(2, "name2"));

        String json = new ObjectMapper().writeValueAsString(appEvents);
        System.out.println(json);

        //employees = new ObjectMapper().readValue(json, new TypeReference<List<Employee>>() {});

        //ObjectMapper objectMapper = new ObjectMapper().getTypeFactory().constructCollectionType(List.class, Employee.class);
        System.out.println(appEvents);
    }

    class MyDeserializer<C, T> implements Deserializer<C> {

        private ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {

        }

        @Override
        public C deserialize(String topic, byte[] data) {
            try {
                objectMapper.readValue(data, new TypeReference<List<AppEvent>>() {});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        public void close() {
        }
    }
}
