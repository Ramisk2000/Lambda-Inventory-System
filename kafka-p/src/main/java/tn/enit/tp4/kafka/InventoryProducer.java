package tn.enit.tp4.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;

public class InventoryProducer {

    private final Producer<String, String> producer;

    public InventoryProducer(Properties properties) {
        this.producer = new KafkaProducer<>(properties);
    }

    public static void main(String[] args) {
        try {
            // Load Kafka properties
            Properties properties = new Properties();
            properties.put("bootstrap.servers", "kafka:9092"); // Adjust host if needed
            properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

            // Initialize the producer
            InventoryProducer inventoryProducer = new InventoryProducer(properties);

            // Send inventory data
            inventoryProducer.sendInventoryData("InventoryData");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendInventoryData(String topic) {
        String filePath = "data/inventory_data.csv"; // Path to the CSV file

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine(); // Skip the header row
            while ((line = reader.readLine()) != null) {
                // Send each line as a Kafka message
                producer.send(new ProducerRecord<>(topic, line));
                System.out.println("Sent: " + line);
                Thread.sleep(1000); // Simulate real-time ingestion
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.close(); // Ensure the producer is closed
        }
    }
}
