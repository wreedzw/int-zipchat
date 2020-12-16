package com.zipwhip.integration.zipchat.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EventType {
  ADDUSER("adduser", "adduser"),
  CHANNELS("channels", "channels"),
  CREATE("creat", "created"),
  DELETE("delet", "deleted"),
  DESCRIBE("describe", "describe"),
  HELP("help", "help"),
  HISTORY("history", "history"),
  INVITE("invite", "invite"),
  JOIN("join", "joined"),
  LEAVE("leave", "left"),
  RENAME("rename", "rename"),
  SUBSCRIBERS("subscribers","subscribers");

  private final String keyword;
  private final String display;
}
