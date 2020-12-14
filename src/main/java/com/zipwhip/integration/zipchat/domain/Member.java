package com.zipwhip.integration.zipchat.domain;

import lombok.Data;

/**
 * The class contains information about a Channel member
 */
@Data
public class Member {

  /**
   * User phone number
   */
  private String mobileNumber;

  /**
   * User selected this name when they joined the channel
   */
  private String name;

  /**
   * This member Opted Out or was banned, do not send messages
   */
  private boolean doNotSend;

}
