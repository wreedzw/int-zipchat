package com.zipwhip.integration.zipchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
    "com.zipwhip.message.service.proxy",
    "com.zipwhip.integration.message",
    "com.zipwhip.environment",
    "com.zipwhip.cloud.client.proxy",
    "com.zipwhip.integration.zipchat",
    "com.zipwhip.legacy.config"
})

/**
 * The spring boot main class which runs the application
 */
@EnableScheduling
@EnableKafka
@EnableFeignClients(basePackages = {
    "com.zipwhip.integration.message",
    "com.zipwhip.cloud.client",
    "com.zipwhip.customerservice.client",
    "com.zipwhip.environment",
    "com.zipwhip.message.service",
    "com.zipwhip.subscription.client"
})
@EnableMongoRepositories(basePackages = {"com.zipwhip.environment", "com.zipwhip.integration.zipchat",
    "com.zipwhip.integration.message", "com.zipwhip.kafka.poller.status.repository",
    "com.zipwhip.integration.lock"})
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
