package com.zipwhip.integration.zipchat.events;

import com.zipwhip.integration.zipchat.domain.Channel;
import com.zipwhip.integration.zipchat.domain.Subscriber;
import com.zipwhip.integration.zipchat.entities.OrgConfig;
import lombok.Value;

@Value
public class Event {
  EventType eventType;

  Subscriber subscriber;

  Channel channel;

  String responseMessage;
}
