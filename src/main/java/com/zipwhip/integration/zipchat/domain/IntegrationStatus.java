package com.zipwhip.integration.zipchat.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * The class contains status information
 */
@Data
public class IntegrationStatus {

    /**
     * Installation value
     */
    private Boolean installed;

    /**
     * Date and time when integration was installed
     */
    private LocalDateTime installedOn;

    /**
     * The name of the user (not username) performing the install
     */
    private String installedBy;

    /**
     * Sync activity value
     */
    private Boolean syncActive;

}