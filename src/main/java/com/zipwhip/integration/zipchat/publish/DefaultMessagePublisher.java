package com.zipwhip.integration.zipchat.publish;

import com.zipwhip.integration.message.TextService;
import com.zipwhip.integration.message.TextServiceWrapper;
import com.zipwhip.integration.message.domain.MessageTracker;
import com.zipwhip.integration.zipchat.domain.Subscriber;
import com.zipwhip.integration.zipchat.domain.SubscriberEvent;
import com.zipwhip.integration.zipchat.repository.SubscriberRepository;
import com.zipwhip.integration.zipchat.service.MessageProcessor;
import com.zipwhip.logging.IntegrationFeature;
import com.zipwhip.message.domain.InboundMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
class DefaultMessagePublisher implements MessagePublisher {

  private final SubscriberRepository subscriberRepository;

  @Autowired
  private TextServiceWrapper textService;

  @Override
  public void publishMessage(Iterable<Subscriber> subscribers, InboundMessage message) {

    final String sourceAddress = message.getPayload().getSourceAddress();
    final String messagePrefix = getMessagePrefix(message);

    subscribers.forEach(x -> {
      if (!x.getMobileNumber().equals(sourceAddress)) {
        //do the send
      }
    });

    // filter out subscriber matching "from" in "message"
    // TODO
  }

  /**
   * Return modified event text for publishing to subscribers
   * @param subEvent
   * @param message
   * @return
   */
  private String transformCommandMessage(SubscriberEvent subEvent, InboundMessage message) {
    String subscriberAlias = subEvent.getSubscription().getSubscriberAlias();
    return subscriberAlias + " "+ subEvent.getEventType().getDisplay();
  }

  public String getMessagePrefix(InboundMessage message) {
    Subscriber subscriber = subscriberRepository.findById(message.getPayload().getSourceAddress()).orElseThrow(IllegalStateException::new);
    return subscriber.getDisplayName() + ":";
  }

  private void sendMessage(String destinationAddress, InboundMessage iMessage) {
    MessageTracker.Origin origin = MessageTracker.Origin.builder()
            .featureName(IntegrationFeature.TEXT_FROM_EXTERNAL.toString())
            .integrationId(22)
            .userGenerated(false)
            .orgCustomerId(iMessage.getOrgCustomerId())
            .build();
    TextService.Response response = textService.send(iMessage.getPayload().getDestAddress(), destinationAddress, getMessagePrefix() + iMessage.getPayload().getBody(), origin);
  }
}
