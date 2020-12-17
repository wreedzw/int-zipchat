package com.zipwhip.integration.zipchat.domain;

import lombok.Data;

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

}
