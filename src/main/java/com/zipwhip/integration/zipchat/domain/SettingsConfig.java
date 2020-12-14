package com.zipwhip.integration.zipchat.domain;

import lombok.Data;

/**
 * This object is used for filling attributes of settings template
 */
@Data
public class SettingsConfig {

    /**
     * The object of integration status {@link com.zipwhip.integration.zipchat.domain.IntegrationStatus}
     */
    private IntegrationStatus status = new IntegrationStatus();

    /**
     * The object of existing settings {@link com.zipwhip.integration.zipchat.domain.Settings}
     */
    private Settings settings = new Settings();

}
