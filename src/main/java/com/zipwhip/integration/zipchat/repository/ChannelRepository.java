package com.zipwhip.integration.zipchat.repository;

import com.zipwhip.integration.zipchat.domain.Channel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChannelRepository extends MongoRepository<Channel, String> {
  Channel findChannelByName(String name);
}
