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
    String channelName = null;
    if (m.groupCount() >= 2) {
      channelName = m.group(2);
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

        case ADDUSER:
        case RENAMEUSER:
           if (m.groupCount() < 2) {
             throw new IllegalArgumentException("missing user name: " + message.getPayload().getBody());
           }
           subscriber = new Subscriber(source, m.group(2));
           event = new SubscriberEvent(eventType, subscriber, channel);
           break;

        case JOINCHANNEL:
          if (m.groupCount() < 2) {
            throw new IllegalArgumentException("missing channel name: " + message.getPayload().getBody());
          }
        case LEAVECHANNEL:
          subscriber = subscriberRepository.findById(source).orElse(null);
          if (subscriber == null) {
            log.warn("Subscriber with phone number {} not found", source);
            return Optional.empty();
          }
          channel = channelRepository.findChannelByName(channelName);
          if (channel == null) {
            log.warn("Specified channel {} not found", channelName);
            return Optional.empty();
          }
          event = new ChannelEvent(eventType, channel);
          break;

        case CREATECHANNEL:
          if (m.groupCount() < 2) {
            throw new IllegalArgumentException("missing channel name: {}" + message.getPayload().getBody());
          }
          event = new ChannelEvent(eventType, new Channel(channelName, channelName, null));
          break;

        case DELETECHANNEL:
          if (m.groupCount() < 2) {
            throw new IllegalArgumentException("missing channel name: " + message.getPayload().getBody());
          }
          channel = channelRepository.findChannelByName(channelName);
          if (channel == null) {
            log.warn("Specified channel {} not found", channelName);
            return Optional.empty();
          }
          event = new ChannelEvent(eventType, channel);
          break;
      }

    }
    return Optional.ofNullable(event);
  }
}
