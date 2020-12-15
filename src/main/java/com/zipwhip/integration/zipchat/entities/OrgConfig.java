package com.zipwhip.integration.zipchat.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** The configuration for an orgCustomer for the ZipChat integration */
@Data
@Document(collection = "ZipChatOrgConfig")
public class OrgConfig {

  /** The orgCustomer's ID */
  @Id Long orgCustomerId;
}
