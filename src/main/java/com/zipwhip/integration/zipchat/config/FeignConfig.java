package com.zipwhip.integration.zipchat.config;

import com.zipwhip.logging.Slf4jFeignLogger;
import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign logging config. Note, this only works for @FeignClient defined clients (which use a builder
 * instantiated by Spring). For cases where Feign.builder() is used, the caller must manually add
 * the logger AND log level
 *
 * @author msokol
 * @see com.zipwhip.integration.salesforce.client.SalesForceClient#getFeignBuilder()
 */
@Configuration
public class FeignConfig {

  /**
   * Sets the log level for any feign clients defined using the @FeignClient annotation
   *
   * @return The level to log at (FULL = request url, response, req/res headers, and bodies)
   */
  @Bean
  Logger.Level feignLoggerLevel() {
    return Logger.Level.FULL;
  }

  /**
   * Sets the logger for any feign clients defined using the @FeignClient annotation
   *
   * @return The logger to use when logging requests
   */
  @Bean
  Logger feignLogger() {
    return new Slf4jFeignLogger();
  }


}
