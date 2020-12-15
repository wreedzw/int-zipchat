package com.zipwhip.integration.zipchat.domain;

import lombok.NonNull;
import lombok.Value;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The class contains information about a Channel member
 */
@Value
@Document("subscriber")
public class Subscriber {

  String id;

  /**
   * User phone number
   */
  @NonNull
  String mobileNumber;

  /**
   * User selected this name when they joined the channel
   */
  @NonNull
  String displayName;

  /**
   * This member Opted Out or was banned, do not send messages
   */
  boolean doNotSend;

}
