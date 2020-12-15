package com.zipwhip.integration.zipchat.events;

import com.zipwhip.integration.zipchat.domain.SubscriberEvent;
import com.zipwhip.message.domain.InboundMessage;
import java.util.Optional;

public class EventDetectorImpl implements EventDetector {

  @Override
  public Optional<SubscriberEvent> detectEvent(InboundMessage message) {
    // FIXME - parse message, create event object or return empty
    // /join zw_dev
    return Optional.empty();
  }
}
