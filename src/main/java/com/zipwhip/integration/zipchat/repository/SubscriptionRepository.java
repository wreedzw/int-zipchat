package com.zipwhip.integration.zipchat.repository;

import com.zipwhip.integration.zipchat.domain.Subscription;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubscriptionRepository extends MongoRepository<Subscription, String> {
  List<Subscription> findBySubscriberId(String subscriberId);

  List<Subscription> findByChannelId(String channelId);

  List<Subscription> deleteByChannelId(String channelId);

  List<Subscription> deleteBySubscriberId(String subscriberId);

  Subscription deleteSubscriptionByChannelIdAndSubscriberId(String channelId, String subscriberId);
}
