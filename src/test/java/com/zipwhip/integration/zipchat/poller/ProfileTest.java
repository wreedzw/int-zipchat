package com.zipwhip.integration.zipchat.poller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import com.zipwhip.subscription.client.SubscriptionInfoClient;
import com.zipwhip.subscription.domain.IntegrationInfo;
import com.zipwhip.subscription.domain.SubscriptionResponse;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("kafka")
@DirtiesContext
class ProfileTestIT {

  @MockBean
  private SubscriptionInfoClient mockSubscriptionInfoClient;

  private SubscriptionResponse<IntegrationInfo> subscriptionResponse;

  @Autowired
  private ApplicationContext applicationContext;

  @BeforeEach
  void setUp() {
    subscriptionResponse = new SubscriptionResponse();
    subscriptionResponse.setResponse(new IntegrationInfo());
    subscriptionResponse.getResponse().setUrl("dummyUrl");
    doReturn(subscriptionResponse).when(mockSubscriptionInfoClient)
      .integrationInfo(any(Long.class));
  }

  @Test
  public void profileTest() {
    assertFalse(
      Arrays.asList(applicationContext.getBeanDefinitionNames()).contains("installController"));
  }
}