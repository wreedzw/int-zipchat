package com.zipwhip.integration.zipchat.service;

import com.zipwhip.integration.zipchat.entities.OrgConfig;
import com.zipwhip.message.domain.InboundMessage;

/**
 * This class is used to perform sync functionality agnostic of the input source. If can be used to
 * sync a message to the CRM (which will sync the contact to Zipwhip) or it can be used to Sync a
 * contact into the CRM
 */
public interface SyncService {

  /**
   * Processes a message sent or received by an integrated landline. This will first find potential
   * contacts by mobile phone. Then it will sync (new and or update) contacts into Zipwhip. Finally,
   * it will attach the message to the appropriate claims in ZipChat.
   *
   * @param message   The message that was sent/received by the landline
   * @param orgConfig The orgConfig for the orgCustomer to which the landline belongs
   */
  public void processMessage(InboundMessage message, OrgConfig orgConfig);

}
