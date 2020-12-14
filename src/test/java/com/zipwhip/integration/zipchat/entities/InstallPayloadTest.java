package com.zipwhip.integration.zipchat.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.zipwhip.subscription.domain.SubscriptionPayload;
import org.junit.jupiter.api.Test;

public class InstallPayloadTest {

  @Test
  public void testToString() {
    InstallPayload payload = new InstallPayload();
    payload.setSubscriptionPayload(new SubscriptionPayload());
    payload.getSubscriptionPayload().setId(2l);
    payload.setToken("weoifhew234234645oifhfo");

    assertEquals("InstallPayload(token=weoifhew234234645oifhfo, subscriptionPayload=" + payload
      .getSubscriptionPayload().toString() + ")", payload.toString());
  }
}
