package com.zipwhip.integration.zipchat.entities;

import com.zipwhip.subscription.domain.SubscriptionPayload;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The details created while performing the various steps of installation. Represents some state and
 * can be used to build an orgConfig at the end of the process
 */
@Data
@ToString
@Document(collection = "ZipChatInstallPayload")
public class InstallPayload {

  /** The token generated for the orgCustomer to invoke the text from external feature */
  @Id String token;

  /** The payload to use/used when invoking int-manager to perform the install */
  SubscriptionPayload subscriptionPayload;
}