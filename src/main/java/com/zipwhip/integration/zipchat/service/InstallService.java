package com.zipwhip.integration.zipchat.service;

import com.zipwhip.integration.zipchat.entities.OrgConfig;
import com.zipwhip.integration.zipchat.error.NoOrgConfigException;
import com.zipwhip.integration.zipchat.repository.OrgConfigRepository;
import com.zipwhip.integration.zipchat.install.InstallController;
import com.zipwhip.integration.zipchat.domain.*;
import com.zipwhip.kafka.poller.status.domain.PollerStatus;
import com.zipwhip.kafka.poller.status.repository.PollerStatusRepository;
import com.zipwhip.logging.IntegrationFeature;
import com.zipwhip.logging.MDC;
import com.zipwhip.logging.MDCFields;
import com.zipwhip.logging.MDCUtil;
import com.zipwhip.subscription.client.SubscriptionInfoClient;
import com.zipwhip.subscription.domain.IntegrationInfo;
import com.zipwhip.subscription.domain.OrgIntegrationInfo.Item;
import com.zipwhip.subscription.domain.SubscriptionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** This service serves {@link InstallController} */
@Service
public class InstallService {

  /** The ID for the integration */
  private final Long integrationId;

  /** The client for getting and creating install records */
  private final SubscriptionInfoClient subscriptionInfoClient;

  /** This repository houses the last known sync state (success/failure with exception) */
  private final PollerStatusRepository pollerStatusRepository;

  /** A repository for managing configurations of orgCustomers */
  private final OrgConfigRepository orgConfigRepository;

  /** The base URL for the integration */
  private String integrationUrl;

  public InstallService(
    @Value("${poller.integrationId}") Long integrationId,
    SubscriptionInfoClient subscriptionInfoClient,
    PollerStatusRepository pollerStatusRepository,
    OrgConfigRepository orgConfigRepository) {
    this.integrationId = integrationId;
    this.subscriptionInfoClient = subscriptionInfoClient;
    this.pollerStatusRepository = pollerStatusRepository;
    this.orgConfigRepository = orgConfigRepository;
    getBaseUrl();
  }

  /**
   * Creates a map for filling template "settings"
   *
   * @param orgCustomerId The orgCustomer Id for whom create map for settings view
   * @return The settingViewConfig
   */
  public SettingsConfig getSettingsConfig(final long orgCustomerId) {
    final SettingsConfig result = new SettingsConfig();
    final Item item =
        subscriptionInfoClient.getInstalledSubscription(orgCustomerId, integrationId).getResponse();

    final Settings existingSettings = result.getSettings();
    final IntegrationStatus status = result.getStatus();
    status.setInstalled(item != null);
    if (item != null) {
      existingSettings.setBaseUrl(item.getUrl());
      status.setInstalledOn(item.getInstalledOn());
      status.setInstalledBy(item.getInstalledBy());
      final PollerStatus pollerStatus = pollerStatusRepository.findById(item.getId()).orElse(null);

      if (pollerStatus != null
          && pollerStatus.getLastDateUpdated() != null
          && pollerStatus.getLastDateUpdated().isAfter(item.getInstalledOn())) {
        status.setSyncActive(pollerStatus.getEnabled());
      }
    }

    final OrgConfig orgConfig =
        orgConfigRepository
            .findById(orgCustomerId)
            .orElseThrow(() -> new NoOrgConfigException(orgCustomerId, integrationId));

    existingSettings.setOrgCustomerId(Long.toString(orgCustomerId));

    return result;
  }

  /**
   * Saves new settings
   *
   * @param orgCustomerId The orgCustomer Id for whom is saving new settings
   * @param newConfig     New settings
   */
  public void saveSettings(final long orgCustomerId, final SettingsConfig newConfig) {
    MDCUtil.startFeature(IntegrationFeature.CONFIG);
    MDC.put(MDCFields.INT_ID, integrationId);
    MDC.put(MDCFields.ORG_CUSTOMER_ID, orgCustomerId);
    final OrgConfig existingConfig = orgConfigRepository.findById(orgCustomerId)
      .orElseThrow(() -> new NoOrgConfigException(orgCustomerId, integrationId));

    final Settings newSettings = newConfig.getSettings();

    orgConfigRepository.save(existingConfig);
  }

  /**
   * Retrieves the URL from the variable if present, but if not gets it from the int-manager
   *
   * @return The base URL for the integration
   */
  private String getBaseUrl() {
    if (this.integrationUrl == null) {
      SubscriptionResponse<IntegrationInfo> info =
          this.subscriptionInfoClient.integrationInfo(integrationId);
      this.integrationUrl =
        info != null && info.getResponse() != null ? info.getResponse().getUrl() : null;
    }
    return this.integrationUrl;
  }

}
