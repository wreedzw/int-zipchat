package com.zipwhip.integration.zipchat.entities;

import com.zipwhip.integration.zipchat.conversation.format.AttachmentFormatter;
import com.zipwhip.integration.zipchat.conversation.format.MessageFormatter;
import com.zipwhip.integration.zipchat.conversation.format.SubjectFormatter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SyncParamsTest {

  @Test
  public void testToString() {
    //Setup
    SyncParams params = SyncParams.DEFAULT;

    //Execute
    String val = params.toString();

    //Validate
    assertEquals(
      "SyncParams(subjectLinePattern=" + SubjectFormatter.DEFAULT_PATTERN + ", messagePattern="
        + MessageFormatter.DEFAULT_PATTERN + ", attachmentPattern="
        + AttachmentFormatter.DEFAULT_PATTERN
        + ", timezone=America/Los_Angeles, contactFields=FormattedContactFields(customField1=null, customField2=null))",
      val);

  }

  @Test
  public void testDefaultConstructor() {
    //Setup
    SyncParams params = new SyncParams();

    //Execute
    String val = params.toString();

    //Validate
    assertEquals(
      "SyncParams(subjectLinePattern=null, messagePattern=null, attachmentPattern=null, " +
        "timezone=null, contactFields=null)", val);
  }
}
