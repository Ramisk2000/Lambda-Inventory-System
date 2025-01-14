package tn.enit.tp4.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kafka.serializer.Encoder;
import kafka.utils.VerifiableProperties;

public class InventoryDataEncoder implements Encoder<InventoryData> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public InventoryDataEncoder(VerifiableProperties verifiableProperties) {}

    @Override
    public byte[] toBytes(InventoryData data) {
        try {
            return objectMapper.writeValueAsString(data).getBytes();
        } catch (JsonProcessingException e) {
            System.err.println("Error serializing InventoryData: " + e.getMessage());
            return null;
        }
    }
}
