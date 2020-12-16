package com.zipwhip.integration.zipchat.service;

import com.zipwhip.integration.zipchat.domain.Subscriber;
import com.zipwhip.integration.zipchat.domain.SubscriberEvent;
import com.zipwhip.integration.zipchat.events.EventDetector;
import com.zipwhip.integration.zipchat.publish.MessagePublisher;
import com.zipwhip.message.domain.InboundMessage;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageProcessor {

  private final SubscriberManager subscriberManager;

  private final ChannelManager channelManager;

  private final EventDetector eventDetector;

  private final MessagePublisher publisher;

  public void process(InboundMessage message) {
    Optional<SubscriberEvent> detectedEvent = eventDetector.detectEvent(message);

    if (detectedEvent.isPresent()) {
      SubscriberEvent se = detectedEvent.get();
      switch (se.getEventType()) {

        case JOIN:
          subscriberManager.updateChannelSubscription(se.getSubscriber(), se.getChannel());
          publisher.publishCommandMessage(se, message);
          break;

        case LEAVE:
          subscriberManager.updateChannelSubscription(se.getSubscriber(), null);
          publisher.publishCommandMessage(se, message);
          break;

        case CREATE:
          // TODO determine if only the landline itself (or anyone) should be able to create channels
          channelManager.createChannel(se.getChannel());
          break;

        case DELETE:
          // TODO determine if only the landline itself (or anyone) should be able delete channels
          channelManager.deleteChannel(se.getChannel());
          break;
      }
    } else {
      try {
        publisher.publishMessage(message);
      } catch (IllegalStateException e) {
        // TODO - put the whole body of this method into try and not just this call, and
        //  add "system" response message informing sender the message was rejected
      }
    }
  }

  private void catchupToChannel(Subscriber subscription, int maxMsgCount) {
    // TODO - send channel history up to maxMsgCount
  }
}
