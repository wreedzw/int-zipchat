package com.zipwhip.integration.zipchat.domain;

import lombok.NonNull;
import lombok.Value;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Value
@Document("subscription")
@CompoundIndex(def = "{ 'channelId' : 1, 'subscriberId': 1 }", unique = true)
public class Subscription {
  @Indexed
  @NonNull
  String channelId;

  @Indexed
  @NonNull
  String subscriberId;

  String subscriberAlias;
}
