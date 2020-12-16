package com.zipwhip.integration.zipchat.publish;

import com.zipwhip.integration.zipchat.events.SubscriberEvent;
import com.zipwhip.message.domain.InboundMessage;

public interface MessagePublisher {

  /**
   * Publish regular message to all subscribers of the channel the sender is member of,
   * except for sender themselves
   * @param message
   */
  void publishMessage(InboundMessage message);

  /**
   * Publish subscribe event to all members of affected channel
   * @param subEvent
   * @param message
   */
  void publishCommandMessage(SubscriberEvent subEvent, InboundMessage message);
}
