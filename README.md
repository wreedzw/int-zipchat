### Hackathon Project: ZipChat

ZipChat is a Zipwhip Integration designed to function as a Slack Channel:
It maintains a list of members
When a member sends a message to the Channel, ZipChat will prepend the username to the message and send it to all members of the Channel

### Keywords

JOIN wreed
LEAVE

### Features

During the ZipChat install the user will be asked for the Channel name
When a user sends the JOIN keyword, ZipChat will respond with "Hello [username], welcome to the [Channel Name] ZipChat Channel


### Design Stages

1. Copy the Zipwhip Guidewire Integration and remove all unnecessary logic
2. Get ZipChat building successful and able to install with no actual functionality
3. Add functionality for the following features:
a. Channel name to the orgConfig
b. Channel name to Install page
b. Member list to the orgConfig
c. Settings page to display Channel name and Member list
d. 

### Running ZipChat Locally

To run ZipChat locally you must clone the int-local-env repo and follow the steps in the readme.md
Once you have Integrations running locally you need to modify the configuration to include ZipChat
Willie will create a feature branch in int-local-env that includes the ZipChat config once ZipChat is running