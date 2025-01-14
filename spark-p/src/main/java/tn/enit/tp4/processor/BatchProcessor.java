package tn.enit.tp4.processor;

import java.util.List;
import java.util.Properties;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.*;
import org.apache.spark.streaming.api.java.JavaDStream;
import tn.enit.tp4.entity.InventoryData;
import tn.enit.tp4.util.PropertyFileReader;

public class BatchProcessor {

    public static void main(String[] args) throws Exception {

        // Load properties
        String file = "spark-processor.properties";
        Properties prop = PropertyFileReader.readPropertyFile(file);

        // Configure Spark
        SparkConf conf = ProcessorUtils.getSparkConf(prop);
        JavaSparkContext sc = new JavaSparkContext(conf);

        // Create a Spark session
        SparkSession sparkSession = SparkSession.builder().config(conf).getOrCreate();

        // Path to the saved inventory data in HDFS
        String saveFile = prop.getProperty("tn.enit.tp4.hdfs") + "inventory-data";

        // Run batch processing to find low stock items
        List<InventoryData> lowStockList = ProcessorUtils.runBatch(sparkSession, saveFile);

        // Print low stock items (for debugging)
        System.out.println("Low Stock Items: ");
        lowStockList.forEach(System.out::println);

        // Save low stock items to Cassandra
        JavaRDD<InventoryData> rdd = sc.parallelize(lowStockList, 1);
        ProcessorUtils.saveLowStockToCassandra(rdd);

        // Close the Spark session
        sparkSession.close();
        sc.close();
    }
}
