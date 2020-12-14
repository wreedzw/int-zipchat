package com.zipwhip.integration.zipchat.entities;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** The configuration for an orgCustomer for the ZipChat integration */
@Data
@ToString
@Document(collection = "ZipChatOrgConfig")
public class OrgConfig {

  /** The orgCustomer's ID */
  @Id Long orgCustomerId;

  /** The name of this Channel */
  String channelName;

  public Long getOrgCustomerId() {
    return orgCustomerId;
  }
}
