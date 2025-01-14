package tn.enit.tp4.processor;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.*;
import org.apache.spark.streaming.api.java.JavaDStream;
import tn.enit.tp4.entity.InventoryData;
import com.datastax.spark.connector.japi.CassandraJavaUtil;

import java.util.List;
import java.util.Properties;

public class ProcessorUtils {

    // Schema for InventoryData
    public static final StructType INVENTORY_DATA_SCHEMA = new StructType()
            .add("skuCode", DataTypes.StringType)
            .add("designNo", DataTypes.StringType)
            .add("stock", DataTypes.IntegerType)
            .add("category", DataTypes.StringType)
            .add("size", DataTypes.StringType)
            .add("color", DataTypes.StringType);

    // Get Spark Configuration from properties
    public static SparkConf getSparkConf(Properties prop) {
        SparkConf sparkConf = new SparkConf()
                .setAppName(prop.getProperty("tn.enit.tp4.spark.app.name"))
                .setMaster(prop.getProperty("tn.enit.tp4.spark.master"))
                .set("spark.cassandra.connection.host", prop.getProperty("tn.enit.tp4.cassandra.host"))
                .set("spark.cassandra.connection.port", prop.getProperty("tn.enit.tp4.cassandra.port"))
                .set("spark.cassandra.auth.username", prop.getProperty("tn.enit.tp4.cassandra.username"))
                .set("spark.cassandra.auth.password", prop.getProperty("tn.enit.tp4.cassandra.password"))
                .set("spark.cassandra.connection.keep_alive_ms", prop.getProperty("tn.enit.tp4.cassandra.keep_alive"));

        if ("local".equals(prop.getProperty("tn.enit.tp4.env"))) {
            sparkConf.set("spark.driver.bindAddress", "127.0.0.1");
        }
        return sparkConf;
    }

    // Save low stock inventory to Cassandra (for streaming)
    public static void saveLowStockToCassandra(final JavaDStream<InventoryData> dataStream) {
        dataStream.filter(data -> data.getStock() < 10).foreachRDD(rdd -> {
            if (!rdd.isEmpty()) {
                CassandraJavaUtil.javaFunctions(rdd)
                        .writerBuilder("ecommerce", "low_stock", CassandraJavaUtil.mapToRow(InventoryData.class))
                        .saveToCassandra();
            }
        });
    }

    // Save inventory data to HDFS (for streaming)
    public static void saveDataToHDFS(final JavaDStream<InventoryData> dataStream, String saveFile, SparkSession sql) {
        dataStream.foreachRDD(rdd -> {
            if (!rdd.isEmpty()) {
                Dataset<Row> dataFrame = sql.createDataFrame(rdd, InventoryData.class)
                        .selectExpr("skuCode", "designNo", "stock", "category", "size", "color");
                dataFrame.write()
                        .mode(SaveMode.Append)
                        .parquet(saveFile);
            }
        });
    }

    // Save low stock inventory to Cassandra (for batch)
    public static void saveLowStockToCassandra(final JavaRDD<InventoryData> data) {
        CassandraJavaUtil.javaFunctions(data)
                .writerBuilder("ecommerce", "low_stock", CassandraJavaUtil.mapToRow(InventoryData.class))
                .saveToCassandra();
    }

    // Run batch processing to calculate low stock inventory
    public static List<InventoryData> runBatch(SparkSession sparkSession, String saveFile) {
        Dataset<Row> dataFrame = sparkSession.read().parquet(saveFile);
        Dataset<InventoryData> inventoryData = dataFrame.as(Encoders.bean(InventoryData.class));

        return inventoryData.filter("stock < 10").collectAsList();
    }
}
