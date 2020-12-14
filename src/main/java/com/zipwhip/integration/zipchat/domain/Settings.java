package com.zipwhip.integration.zipchat.domain;

import lombok.Data;

import java.util.ArrayList;

/**
 * The class contains information about values of existing settings
 */
@Data
public class Settings {

    /**
     * The base url of the service
     */
    private String baseUrl;

    /**
     * The org customer ID for the user whose settings these are
     */
    private String orgCustomerId;

    /**
     * Info about individual member of the Channel
     */
    private class Member {
        private String mobileNumber;

        /**
         * User selected this name when they joined the channel
         */
        private String name;

        /**
         * This member Opted Out or was banned, do not send messages
         */
        private boolean doNotSend;
    }

    /**
     * List of members of the Channel
     */
    private ArrayList<Member> Members;
}
