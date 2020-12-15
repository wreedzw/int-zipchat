package com.zipwhip.integration.zipchat.service;

import com.zipwhip.integration.zipchat.domain.Channel;
import com.zipwhip.integration.zipchat.domain.Subscriber;
import com.zipwhip.integration.zipchat.domain.Subscription;
import com.zipwhip.integration.zipchat.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class SubscriptionManager {

  private final SubscriptionRepository subscriptionRepository;

  Iterable<Subscriber> getChannelSubscribers(String channelName){
    // todo
    return null;
  }

  Subscription addSubscriber(Subscriber sub, Channel channel) {
    return addSubscriber(sub, channel, sub.getDisplayName());
  }

  Subscription addSubscriber(Subscriber sub, Channel channel, String subAlias) {
    Subscription subscription = new Subscription(channel.getId(), sub.getId(), subAlias);
    return subscriptionRepository.save(subscription);
  }

  Subscription deleteSubscription(Subscriber subscriber, Channel channel) {
    return subscriptionRepository.deleteSubscriptionByChannelIdAndSubscriberId(channel.getId(),
        subscriber.getId());
  }

  void subscriberRemoved(Subscriber sub) {
    subscriptionRepository.deleteBySubscriberId(sub.getId());
  }

  void channelRemoved(Channel channel) {
    subscriptionRepository.deleteByChannelId(channel.getId());
  }
}
