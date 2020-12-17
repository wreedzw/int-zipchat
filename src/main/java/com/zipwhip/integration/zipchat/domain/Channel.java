package com.zipwhip.integration.zipchat.domain;

import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("channel")
@Value
@NonFinal
public class Channel {

  String id;

  @Indexed
  @NonNull
  String name;

  String description;
}