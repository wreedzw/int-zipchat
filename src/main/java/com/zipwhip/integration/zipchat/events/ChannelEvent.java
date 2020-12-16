package com.zipwhip.integration.zipchat.events;

import com.zipwhip.integration.zipchat.domain.Channel;
import lombok.Value;

@Value
public class ChannelEvent implements Event{
  EventType eventType;

  Channel channel;

}
