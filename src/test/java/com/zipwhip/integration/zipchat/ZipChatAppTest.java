package com.zipwhip.integration.zipchat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

import com.mongodb.MongoClient;
import com.zipwhip.subscription.client.SubscriptionInfoClient;
import com.zipwhip.subscription.domain.IntegrationInfo;
import com.zipwhip.subscription.domain.SubscriptionResponse;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://127.0.0.1:1234",
  "port=1234", "auto.create.topics.enable=false"})
@ActiveProfiles("test")
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class ZipChatAppTest {


  @Test
  public void contextLoads() {
  }


  @Autowired
  private MongoTemplate mongo;

  @Autowired
  private MongoClient client;

  @MockBean
  private SubscriptionInfoClient mockSubscriptionInfoClient;

  private SubscriptionResponse<IntegrationInfo> subscriptionResponse;

  @Before
  public void setup() {

    subscriptionResponse = new SubscriptionResponse();
    subscriptionResponse.setResponse(new IntegrationInfo());
    subscriptionResponse.getResponse().setUrl("dummyUrl");
    doReturn(subscriptionResponse).when(mockSubscriptionInfoClient)
      .integrationInfo(any(Long.class));
  }

}
