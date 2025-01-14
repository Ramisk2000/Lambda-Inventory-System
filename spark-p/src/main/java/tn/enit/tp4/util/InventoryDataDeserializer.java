package tn.enit.tp4.util;

import tn.enit.tp4.entity.InventoryData;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class InventoryDataDeserializer implements Deserializer<InventoryData> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public InventoryData deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, InventoryData.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public void close() {}
}
