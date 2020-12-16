### Hackathon Project: ZipChat

ZipChat is a Zipwhip Integration designed to function like a Slack Channel.
It maintains a list of Channels, and maintains a list of Subscribers in each Channel.
When the Zipwhip landline receives an incoming message ZipChat performs the following:
* verify the message source is a member of a Channel
* determine which Channel the user is a Subscriber of
* prepend the username to the message
* send the message to all members of that Channel

### Keywords

JOIN [channelname] [username]

LEAVE

CREATE [channelname]

DELETE [channelname]

### Current Features

ZipChat supports creating multiple Channels, however a user may only be subscribed to one Channel at a time.
A user must be subscribed to a Channel in order for the messages they send to the landline to be sent to all
Channel subscribers.

### Future Features

1. ZipChat Integration Settings page will support Channel management (to create, rename, and delete Channels)
and Subscriber management (delete Subscribers, ban Subscribers preventing them from rejoining).

2. New keywords:
* SUBSCRIBERS sends a list of all Channel members with their phone number.
* CHANNELS sends a list of all Channels.
* HISTORY sends Channel messages from prior to the Subscriber joining.
* INVITE allows a Subscriber to have ZipChat invite someone to the Channel.
* HELP sends a message with all valid keywords.

### Potential Bluesteel Support

* The Zipwhip Saas App may consider adding functionality to the Message Feed allowing messages to be
organized by Channel. This would permit a subscriber to join multiple Channels and easily read all
messages of a single Channel.

### Running ZipChat Locally

To run ZipChat locally you must clone https://github.com/Zipwhip/int-local-env and follow the steps
in the readme.md to stand up the base services. Then modify the configuration to include ZipChat
