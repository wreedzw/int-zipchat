package com.zipwhip.integration.zipchat.events;

import com.zipwhip.integration.zipchat.domain.Channel;
import com.zipwhip.integration.zipchat.domain.Subscriber;
import com.zipwhip.integration.zipchat.events.Event;
import com.zipwhip.integration.zipchat.events.EventType;
import lombok.Value;

@Value
public class SubscriberEvent implements Event {
  EventType eventType;

  Subscriber subscriber;

  Channel channel;
}
