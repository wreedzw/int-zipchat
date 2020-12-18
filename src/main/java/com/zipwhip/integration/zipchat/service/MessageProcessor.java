package com.zipwhip.integration.zipchat.service;

import com.zipwhip.integration.zipchat.events.Event;
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

        switch (event.getEventType()) {

          case CREATE:
            channelManager.createChannel(event.getChannel());
            publisher.publishToSender(event, message, event.getResponseMessage());
            break;

          case DELETE:
            channelManager.deleteChannel(event.getChannel());
            publisher.publishToSender(event, message, event.getResponseMessage());
            break;

          case DESCRIBE:
            publisher.publishToSender(event, message, event.getResponseMessage());
            break;

          case HELP:
            publisher.publishToSender(event, message, event.getResponseMessage());
            break;

          case HISTORY:
            publisher.publishToSender(event, message, event.getResponseMessage());

          case INVITE:
            publisher.publishToSender(event, message, event.getResponseMessage());
            break;

          case JOIN:
            subscriberManager.updateChannelSubscription(event.getChannel(), event.getSubscriber());
            publisher.publishCommandMessage(event, message);
            break;

          case LEAVE:
            subscriberManager.updateChannelSubscription(null, event.getSubscriber());
            publisher.publishCommandMessage(event, message);
            break;

          case LISTCHANNELS:
            publisher.publishToSender(event, message, event.getResponseMessage());
            break;

          case LISTSUBSCRIBERS:
            publisher.publishToSender(event, message, event.getResponseMessage());
            break;

          case RENAMECHANNEL:
            publisher.publishCommandMessage(event, message);
            break;

          case RENAMESUBSCRIBER:
            subscriberManager.addSubscriber(event.getSubscriber());
            publisher.publishCommandMessage(event, message);
            break;

          case SETDESCRIPTION:
            publisher.publishToSender(event, message, event.getResponseMessage());
            break;

          case SILENT:
            publisher.publishToSender(event, message, event.getResponseMessage());
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