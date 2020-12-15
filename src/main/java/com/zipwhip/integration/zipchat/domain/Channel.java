package com.zipwhip.integration.zipchat.domain;

import lombok.Value;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("channel")
@Value
public class Channel {
  String id;

  String name;
}
