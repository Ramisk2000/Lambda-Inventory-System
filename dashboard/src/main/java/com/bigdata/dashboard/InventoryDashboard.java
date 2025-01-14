package com.bigdata.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;



@SpringBootApplication
@EnableScheduling
@EnableCassandraRepositories("com.bigdata.dashboard.repository")
public class InventoryDashboard {
    public static void main(String[] args) {
        SpringApplication.run(InventoryDashboard.class, args);
    }
}

