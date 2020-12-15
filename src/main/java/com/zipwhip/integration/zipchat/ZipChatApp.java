package com.zipwhip.integration.zipchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The spring boot main class which runs the application
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.zipwhip.kafka.config",
    "com.zipwhip.service",
})

/**
 * The spring boot main class which runs the application
 */
@SpringCloudApplication
@EnableCaching
@EnableScheduling
@EnableKafka
@EnableFeignClients(basePackages = {
    "com.zipwhip.cloud.client",
    "com.zipwhip.customerservice.client",
    "com.zipwhip.environment",
    "com.zipwhip.subscription.client"
})
@EnableMongoRepositories
public class ZipChatApp { // NOSONAR

  /**
   * The applications main methods
   *
   * @param args The command line args to run the application with
   */
  public static void main(String[] args) {
    SpringApplication.run(ZipChatApp.class, args); // NOSONAR
  }

}
