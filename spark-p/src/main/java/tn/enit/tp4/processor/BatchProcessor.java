package tn.enit.tp4.processor;

import java.util.List;
import java.util.Properties;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import tn.enit.tp4.entity.AverageData;
import tn.enit.tp4.util.PropertyFileReader;


public class BatchProcessor {

    public static void main(String[] args) throws Exception {

        String file = "spark-processor.properties";
        Properties prop = PropertyFileReader.readPropertyFile(file);
        SparkConf conf = ProcessorUtils.getSparkConf(prop);
        JavaSparkContext sc = new JavaSparkContext(conf);

        SparkSession sparkSession = SparkSession.builder().config(conf).getOrCreate();

        String saveFile = prop.getProperty("tn.enit.tp4.hdfs")+ "iot-data"; ;

        List<AverageData> average_data_list = ProcessorUtils.runBatch(sparkSession, saveFile);
        System.out.println("Data to persist");
        JavaRDD<AverageData> h = sc.parallelize(average_data_list, 1); // transform to RDD
        ProcessorUtils.saveAvgToCassandra(h);

        sparkSession.close();
        sparkSession.stop();

    }

}

