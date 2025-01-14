package tn.enit.tp4.processor;

import java.util.*;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka010.*;
import tn.enit.tp4.entity.InventoryData;
import tn.enit.tp4.util.InventoryDataDeserializer;
import tn.enit.tp4.util.PropertyFileReader;
import org.apache.spark.sql.SparkSession;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

public class StreamProcessor {

    public static void main(String[] args) throws Exception {

        // Load properties
        String file = "spark-processor.properties";
        Properties prop = PropertyFileReader.readPropertyFile(file);

        // Configure Spark
        SparkConf conf = ProcessorUtils.getSparkConf(prop);

        // Create a streaming context
        JavaStreamingContext streamingContext = new JavaStreamingContext(conf, Durations.seconds(10));
        streamingContext.checkpoint(prop.getProperty("tn.enit.tp4.spark.checkpoint.dir"));

        // Kafka consumer parameters
        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, prop.getProperty("tn.enit.tp4.brokerlist"));
        kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, InventoryDataDeserializer.class);
        kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, "inventory-stream-group");
        kafkaParams.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, prop.getProperty("tn.enit.tp4.resetType"));
        kafkaParams.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        // Subscribe to Kafka topic
        Collection<String> topics = Arrays.asList(prop.getProperty("tn.enit.tp4.topic"));

        // Create a Kafka stream
        JavaInputDStream<ConsumerRecord<String, InventoryData>> stream = KafkaUtils.createDirectStream(
                streamingContext,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.<String, InventoryData>Subscribe(topics, kafkaParams)
        );

        // Extract the InventoryData from the stream
        JavaDStream<InventoryData> inventoryStream = stream.map(ConsumerRecord::value);

        // Process the data
        ProcessorUtils.saveLowStockToCassandra(inventoryStream);
        ProcessorUtils.saveDataToHDFS(inventoryStream, prop.getProperty("tn.enit.tp4.hdfs"), SparkSession.builder().config(conf).getOrCreate());

        // Start the streaming context
        streamingContext.start();
        streamingContext.awaitTermination();
    }
}
