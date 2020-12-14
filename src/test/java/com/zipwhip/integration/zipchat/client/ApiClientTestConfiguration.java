package com.zipwhip.integration.zipchat.client;

import static org.mockito.Mockito.mock;

import com.zipwhip.subscription.client.SubscriptionInfoClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ApiClientTestConfiguration {

  @Bean
  public SubscriptionInfoClient subscriptionInfoClient() {
    return mock(SubscriptionInfoClient.class);
  }

}
