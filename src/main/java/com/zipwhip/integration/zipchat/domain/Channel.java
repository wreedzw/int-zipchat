package com.zipwhip.integration.zipchat.domain;

import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;
import lombok.Value;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("channel")
@Value
public class Channel {

  String id;

  @Indexed
  @NonNull
  String name;
}