package com.zipwhip.integration.zipchat.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EventType {
  JOIN("join"), LEAVE("leave"), JOIN_AS("joinAs");

  private final String keyword;
}
