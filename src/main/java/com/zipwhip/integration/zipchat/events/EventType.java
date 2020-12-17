package com.zipwhip.integration.zipchat.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EventType {
  ADDUSER("adduser", "user added"),
  CHANNELS("channels", "channels"),
  CREATECHANNEL("createchannel", "channel created"),
  DELETECHANNEL("deletechannel", "channel deleted"),
  DESCRIBE("describe", "describe"),
  HELP("help", "help"),
  HISTORY("history", "history"),
  INVITE("invite", "invite"),
  JOINCHANNEL("joinchannel", "channel joined"),
  LEAVECHANNEL("leavechannel", "channel left"),
  RENAMECHANNEL("renamechannel", "rename channel"),
  RENAMEUSER("renameuser", "rename user"),
  SUBSCRIBERS("subscribers","subscribers");

  private final String keyword;
  private final String display;
}
