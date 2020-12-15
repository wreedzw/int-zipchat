package com.zipwhip.integration.zipchat;

import com.zipwhip.kafka.config.KafkaConfig;
import com.zipwhip.service.MessageProcessingRecorder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The spring boot main class which runs the application
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.zipwhip.environment",
        "com.zipwhip.integration.message",
        "com.zipwhip.kafka.config",
        "com.zipwhip.message.service",
        "com.zipwhip.security",
        "com.zipwhip.service",
        "com.zipwhip.subscription.client"
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
    "com.zipwhip.integration.message",
    "com.zipwhip.message.service",
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
