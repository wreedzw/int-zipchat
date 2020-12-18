package com.zipwhip.integration.zipchat.events;

import com.zipwhip.integration.zipchat.domain.Channel;
import com.zipwhip.integration.zipchat.domain.Subscriber;
import com.zipwhip.integration.zipchat.repository.ChannelRepository;
import com.zipwhip.integration.zipchat.repository.SubscriberRepository;
import com.zipwhip.message.domain.InboundMessage;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventDetectorImpl implements EventDetector {

  private static final Pattern CMD_PATTERN = Pattern.compile("^/(\\w+)\\s+(\\w+)(\\s+\\w+)?$");

  private final SubscriberRepository subscriberRepository;

  private final ChannelRepository channelRepository;

  @Override
  public Optional<Event> detectEvent(InboundMessage message) {

    Matcher m = CMD_PATTERN.matcher(message.getPayload().getBody().trim());
    Event event = null;
    EventType eventType = null;
    Subscriber subscriber = null;
    Channel channel = null;
    String name = null;
    String responseMessage = null;

    if (m.groupCount() >= 2) {
      // either channel name or subscriber name
      name = m.group(2);
    }

    if (m.matches()) {
      for (EventType e : EventType.values()) {
        if (e.getKeyword().equalsIgnoreCase(m.group(1))) {
          eventType = e;
          break;
        }
      }

      if (eventType == null) return Optional.empty();

      String source = message.getPayload().getSourceAddress();

      switch (eventType) {

        case CREATE:
          if (m.groupCount() < 2) {
            responseMessage = "Unable to create, missing channel name";
          }
          channel = channelRepository.findChannelByName(name);
          if (channel != null) {
            responseMessage = "Unable to create, channel " + name + " already exists";
          }
          break;

        case DELETE:
          if (m.groupCount() < 2) {
            responseMessage = "Unable to delete, missing channel name";
          }
          channel = channelRepository.findChannelByName(name);
          if (channel == null) {
            responseMessage = "Unable to delete, channel name " + name + " was not found";
          }
          break;

        case DESCRIBE:
          responseMessage = "/describe is not yet implemented";
          break;

        case HELP:
          responseMessage = "/help is not yet implemented";
          break;

        case HISTORY:
          responseMessage = "/history is not yet implemented";
          break;

        case INVITE:
          responseMessage = "/invite is not yet implemented";
          break;

        case JOIN:
          if (m.groupCount() < 2) {
            responseMessage = "unable to join, missing channel name";
          }
          subscriber = subscriberRepository.findById(source).orElse(null);
          if (subscriber == null) {
            responseMessage = "subscriber with phone number " + source + " not found";
          }
          channel = channelRepository.findChannelByName(name);
          if (channel == null) {
            responseMessage = "channel " + name + " not found";
          }
          break;

        case LEAVE:
          subscriber = subscriberRepository.findById(source).orElse(null);
          if (subscriber == null) {
            responseMessage = "subscriber with phone number " + source + " not found";
          }
          channel = channelRepository.findChannelByName(name);
          if (channel == null) {
            responseMessage = "channel " + name + " not found";
          }
          break;

        case LISTCHANNELS:
          responseMessage = "/listchannels is not yet implemented";
          break;

        case LISTSUBSCRIBERS:
          responseMessage = "/listsubscribers is not yet implemented";
          break;

        case RENAMECHANNEL:
          responseMessage = "/renamechannel is not yet implemented";
          break;

        case RENAMESUBSCRIBER:
           if (m.groupCount() < 2) {
             responseMessage = "Unable to rename subscriber, missing subscriber name";
           }
           subscriber = new Subscriber(source, name);
           break;

        case SETDESCRIPTION:
          responseMessage = "/setdescription is not yet implemented";
          break;

        case SILENT:
          responseMessage = "/silent is not yet implemented";
          break;
      }
      event = new Event(eventType, subscriber, channel, responseMessage);

    }
    return Optional.ofNullable(event);
  }
}
