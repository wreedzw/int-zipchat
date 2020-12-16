package com.zipwhip.integration.zipchat.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EventType {
  JOIN("join", "joined"), LEAVE("leave","left"), JOIN_AS("joinAs", "joined");

  private final String keyword;
  private final String display;
}
