package com.zipwhip.integration.zipchat.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EventType {
  JOIN("join", "joined"), LEAVE("leave","left"), CREATE("creat","created"), DELETE("delet","deleted");

  private final String keyword;
  private final String display;
}
