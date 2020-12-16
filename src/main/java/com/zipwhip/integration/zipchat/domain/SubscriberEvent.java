package com.zipwhip.integration.zipchat.domain;

import com.zipwhip.integration.zipchat.events.EventType;
import lombok.Value;

@Value
public class SubscriberEvent {
  EventType eventType;

  Subscriber subscriber;

  Channel channel;
}
