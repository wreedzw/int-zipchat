package com.zipwhip.integration.zipchat.publish;

import com.zipwhip.integration.zipchat.domain.Subscriber;
import com.zipwhip.message.domain.InboundMessage;

public interface MessagePublisher {

  void publishMessage(Iterable<Subscriber> subscribers, InboundMessage message);

}
