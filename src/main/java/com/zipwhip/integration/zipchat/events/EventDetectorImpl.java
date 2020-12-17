package com.zipwhip.integration.zipchat.events;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.zipwhip.integration.zipchat.domain.Channel;
import com.zipwhip.integration.zipchat.domain.Subscriber;
import com.zipwhip.integration.zipchat.domain.SubscriberEvent;
import com.zipwhip.integration.zipchat.repository.ChannelRepository;
import com.zipwhip.integration.zipchat.repository.SubscriberRepository;
import com.zipwhip.message.domain.InboundMessage;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventDetectorImpl implements EventDetector {

  private static final Pattern CMD_PATTERN = Pattern.compile("^/(\\w+)\\s+(\\w+)(\\s+\\w+)?$");


  private final SubscriberRepository subscriberRepository;

  private final ChannelRepository channelRepository;

  @Override
  public Optional<SubscriberEvent> detectEvent(InboundMessage message) {

    Matcher m = CMD_PATTERN.matcher(message.getPayload().getBody().trim());
    SubscriberEvent subEvent = null;

    if (m.matches()) {
      String cmd = m.group(1);
      String channelName = m.group(2);

      EventType evtType = null;

      for (EventType e: EventType.values()) {
        if (e.getKeyword().equalsIgnoreCase(cmd)) {
          evtType = e;
          break;
        }
      }

      if (evtType == null) return Optional.empty();

      String src = message.getPayload().getSourceAddress();
      Subscriber s = subscriberRepository.findById(src).orElse(null);
      if (s == null) {
        if (m.group(3) == null) {
          log.warn("Subscriber with phone number {} not found and no alias", src);
          return Optional.empty();
        } else {
          log.info("Creating Subscriber {} on the fly", m.group(3));
          s = new Subscriber(message.getPayload().getSourceAddress(), m.group(3).trim());
        }
      }

      Channel c = channelRepository.findChannelByName(channelName);
      if (c == null) {
        log.warn("Specified channel {} not found", channelName);
        return Optional.empty();
      }

      subEvent = new SubscriberEvent(evtType, s, c);
    }
    return Optional.ofNullable(subEvent);
  }
}
