package com.zipwhip.integration.zipchat.publish;

import com.zipwhip.integration.zipchat.events.Event;
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
  void publishCommandMessage(Event subEvent, InboundMessage message);

  /**
   * Publish event to the sender
   * @param subEvent
   */
  void publishToSender(Event subEvent, InboundMessage message, String overrideMessage);
}
