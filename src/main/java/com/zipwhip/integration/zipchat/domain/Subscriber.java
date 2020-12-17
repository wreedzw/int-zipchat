package com.zipwhip.integration.zipchat.domain;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The class contains information about a Channel member
 */
@Data
@Document("subscriber")
public class Subscriber {

  /**
   * User phone number
   */
  @NonNull
  @Id
  private final String mobileNumber;

  /**
   * User selected this name when they joined the channel
   */
  @NonNull
  private final String displayName;

  /**
   * This member Opted Out or was banned, do not send messages
   */
  boolean doNotSend;

  @Indexed
  String channelId;
}
