package com.zipwhip.integration.zipchat.service;

import com.zipwhip.integration.zipchat.domain.Channel;
import com.zipwhip.integration.zipchat.domain.Subscriber;
import com.zipwhip.integration.zipchat.repository.ChannelRepository;
import com.zipwhip.integration.zipchat.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ChannelManager {

  private final ChannelRepository channelRepository;

  Channel findChannelByName(String channelName){
    return channelRepository.findChannelByName(channelName);
  }

  void createChannel(Channel c) {
    channelRepository.save(c);
  }

  void deleteChannel(Channel c) {
    channelRepository.delete(c);
  }
}
