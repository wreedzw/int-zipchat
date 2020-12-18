package com.zipwhip.integration.zipchat.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EventType {
  CREATE("createchannel", "channel created"),
  DELETE("deletechannel", "channel deleted"),
  DESCRIBE("describe", "describe"),
  HELP("help", "help"),
  HISTORY("history", "history"),
  INVITE("invite", "invite"),
  JOIN("joinchannel", "channel joined"),
  LEAVE("leavechannel", "channel left"),
  LISTCHANNELS("listchannels", "channels listed"),
  LISTSUBSCRIBERS("listsubscribers", "subscribers listed"),
  RENAMECHANNEL("renamechannel", "rename channel"),
  RENAMESUBSCRIBER("renameuser", "rename user"),
  SETDESCRIPTION("setdescription", "description set"),
  SILENT("silent", "silent set");

  private final String keyword;
  private final String display;
}
