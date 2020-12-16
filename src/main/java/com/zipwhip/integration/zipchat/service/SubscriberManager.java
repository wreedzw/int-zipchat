package com.zipwhip.integration.zipchat.service;

import com.zipwhip.integration.zipchat.domain.Channel;
import com.zipwhip.integration.zipchat.domain.Subscriber;
import com.zipwhip.integration.zipchat.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class SubscriberManager {

  private final SubscriberRepository subscriberRepository;

  Iterable<Subscriber> getChannelSubscribers(String channelId){
    return subscriberRepository.findByChannelId(channelId);
  }

  void updateChannelSubscription(Subscriber sub, Channel c) {
    sub.setChannelId(c == null ? null : c.getId());

    subscriberRepository.save(sub);
  }

  Subscriber getSubscriber(String srcAddress) {
    return subscriberRepository.findById(srcAddress).orElseThrow(IllegalStateException::new);
  }

  void addSubscriber(Subscriber sub) {
    subscriberRepository.save(sub);
  }

  void removeSubscriber(Subscriber sub) {
    subscriberRepository.delete(sub);
  }
}
