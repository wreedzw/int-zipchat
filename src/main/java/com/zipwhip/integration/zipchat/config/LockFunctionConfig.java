package com.zipwhip.integration.zipchat.config;

import com.zipwhip.integration.lock.LockFunction;
import com.zipwhip.integration.lock.TaskLockRepository;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LockFunctionConfig {

  @Value("${spring.application.name}")
  private String SERVICE_NAME;

  @Bean
  public LockFunction lockFunction(TaskLockRepository taskLockRepository) {
    return new LockFunction(taskLockRepository, Duration.ofMinutes(30), SERVICE_NAME);
  }
}
