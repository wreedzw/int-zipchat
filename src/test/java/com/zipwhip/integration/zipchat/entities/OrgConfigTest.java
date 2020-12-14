package com.zipwhip.integration.zipchat.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrgConfigTest {

  @Test
  public void testToString() {
    OrgConfig config = new OrgConfig();
    config.setOrgCustomerId(5l);
    config.setAuth(new AuthenticationCredentials());
    config.getAuth().setPassword("someP@ss");
    config.getAuth().setUsername("user");

    Assertions.assertEquals(
      "OrgConfig(orgCustomerId=5, auth=AuthenticationCredentials(username=user, password=someP@ss, gwBaseUrl=null), syncParams=null, zipwhipToken=null, conversationWindow=null, locale=null, traceFieldName=null, contactSyncFromCrmEnabled=true, messageArchiveToCrmEnabled=true, textFromCrmEnabled=true)",
      config.toString());
  }
}
