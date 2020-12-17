package com.zipwhip.integration.zipchat.service;

import com.zipwhip.integration.zipchat.events.ChannelEvent;
import com.zipwhip.integration.zipchat.events.Event;
import com.zipwhip.integration.zipchat.events.SubscriberEvent;
import com.zipwhip.integration.zipchat.events.EventDetector;
import com.zipwhip.integration.zipchat.publish.MessagePublisher;
import com.zipwhip.message.domain.InboundMessage;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageProcessor {

  private final SubscriberManager subscriberManager;

  private final ChannelManager channelManager;

  private final EventDetector eventDetector;

  private final MessagePublisher publisher;

  public void process(InboundMessage message) {
    Optional<Event> detectedEvent = eventDetector.detectEvent(message);
    try {

      if (detectedEvent.isPresent()) {
        Event event = detectedEvent.get();
        SubscriberEvent subscriberEvent = (SubscriberEvent) event;
        ChannelEvent channelEvent = (ChannelEvent) event;

        switch (event.getEventType()) {

          case ADDUSER:
          case RENAMEUSER:
            // TODO determine if only the landline itself (or anyone) should be able to add users
            subscriberManager.addSubscriber(subscriberEvent.getSubscriber());
            break;

          case JOINCHANNEL:
            subscriberManager.updateChannelSubscription(channelEvent.getChannel(), subscriberEvent.getSubscriber());
            publisher.publishCommandMessage(subscriberEvent, message);
            break;

          case LEAVECHANNEL:
            subscriberManager.updateChannelSubscription(null, subscriberEvent.getSubscriber());
            publisher.publishCommandMessage(subscriberEvent, message);
            break;

          case CREATECHANNEL:
            // TODO determine if only the landline itself (or anyone) should be able to create channels
            channelManager.createChannel(channelEvent.getChannel());
            break;

          case DELETECHANNEL:
            // TODO determine if only the landline itself (or anyone) should be able delete channels
            channelManager.deleteChannel(channelEvent.getChannel());
            break;
        }
      } else {
        try {
          publisher.publishMessage(message);
        } catch (RuntimeException re) {
          log.error("Failed to process message {}", message, re);
          // TODO - add "system" response message informing sender the message was rejected
        }
      }

    } catch (Exception e) {
      // TODO handle exception
      log.error("MessageProcessor failed {}", message, e);
    }
  }
}