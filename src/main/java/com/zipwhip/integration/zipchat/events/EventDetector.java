package com.zipwhip.integration.zipchat.events;

import com.zipwhip.message.domain.InboundMessage;
import java.util.Optional;

public interface EventDetector {

  /**
   *
   * @param message
   * @return event from message, with participants filled in
   */
  Optional<Event> detectEvent(InboundMessage message);
}
