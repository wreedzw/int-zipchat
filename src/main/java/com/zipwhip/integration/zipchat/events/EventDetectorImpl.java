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

  private static final Pattern CMD_PATTERN = Pattern.compile("^/(\\w+)\\s+(\\w+)$");

  private final SubscriberRepository subscriberRepository;

  private final ChannelRepository channelRepository;

  @Override
  public Optional<Event> detectEvent(InboundMessage message) {

    Matcher m = CMD_PATTERN.matcher(message.getPayload().getBody().trim());
    SubscriberEvent event = null;

    if (m.matches()) {
      String cmd = m.group(1);

      EventType evtType = null;

      for (EventType e : EventType.values()) {
        if (e.getKeyword().equalsIgnoreCase(cmd)) {
          evtType = e;
          break;
        }
      }

      if (evtType == null) return Optional.empty();

      String src = message.getPayload().getSourceAddress();
      Subscriber s = null;
      Channel c = null;
      ChannelEvent ce = null;

      switch (evtType) {

        case ADDUSER:
        case RENAME:
           if (m.groupCount() < 2) {
             throw new IllegalArgumentException("missing username value: " + message.getPayload().getBody());
           }
           s = new Subscriber(src, m.group(2));
           break;

        case JOIN:
        case LEAVE:
          s = subscriberRepository.findById(src).orElse(null);
          if (s == null) {
            log.warn("Subscriber with phone number {} not found", src);
            return Optional.empty();
          }
          String channelName = m.group(2);
          c = channelRepository.findChannelByName(channelName);
          if (c == null) {
            log.warn("Specified channel {} not found", channelName);
            return Optional.empty();
          }
          break;

        case CREATE:
        case DELETE:
          if (m.groupCount() < 2) {
            throw new IllegalArgumentException("missing username value: " + message.getPayload().getBody());
          }
          ce = new ChannelR
          break;
      }

      event = new SubscriberEvent(evtType, s, c);
    }
    return Optional.ofNullable(event);
  }
}
