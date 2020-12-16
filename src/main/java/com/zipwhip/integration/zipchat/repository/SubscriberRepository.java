package com.zipwhip.integration.zipchat.repository;

import com.zipwhip.integration.zipchat.domain.Subscriber;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubscriberRepository extends MongoRepository<Subscriber, String> {
  List<Subscriber> findByChannelId(String channelId);
}
