package com.zipwhip.integration.zipchat.service;

import com.zipwhip.integration.zipchat.domain.SubscriberEvent;
import com.zipwhip.integration.zipchat.domain.Subscription;
import com.zipwhip.integration.zipchat.events.EventDetector;
import com.zipwhip.integration.zipchat.publish.MessagePublisher;
import com.zipwhip.message.domain.InboundMessage;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageProcessor {

  private final SubscriptionManager subscriptionManager;

  private final EventDetector eventDetector;

  private final MessagePublisher publisher;

  public void process(InboundMessage message) {
    Optional<SubscriberEvent> detectedEvent = eventDetector.detectEvent(message);

    if (detectedEvent.isPresent()) {
      switch (detectedEvent.get().getEventType()) {
        case JOIN:

          // subscriptionManager.addSubscriber()
          break;
        case JOIN_AS:
          // subscriptionManager.addSubscriber()
          break;
        case LEAVE:
          // subscriptionManager.deleteSubscription()
          break;
      }
    }


    publisher.publishMessage(subscriptionManager.getChannelSubscribers(extractChannelName(message)), message);
  }

  private void catchupToChannel(Subscription subscription, int maxMsgCount) {
    // TODO - send channel history up to maxMsgCount
  }

  private String extractChannelName(InboundMessage message) {
    // FIXME
    return null;
  }
}
