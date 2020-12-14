package com.zipwhip.integration.zipchat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.zipwhip.integration.zipchat.entities.OrgConfig;
import com.zipwhip.integration.zipchat.repository.OrgConfigRepository;
import com.zipwhip.kafka.poller.status.domain.PollerStatus;
import com.zipwhip.kafka.poller.status.repository.PollerStatusRepository;
import com.zipwhip.smssync.domain.TimezoneList;
import com.zipwhip.subscription.client.SubscriptionInfoClient;
import com.zipwhip.subscription.domain.IntegrationInfo;
import com.zipwhip.subscription.domain.OrgIntegrationInfo;
import com.zipwhip.subscription.domain.OrgIntegrationInfo.Item;
import com.zipwhip.subscription.domain.SubscriptionResponse;
import java.time.LocalDateTime;
import java.util.Optional;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class InstallServiceTest {

  private static final long ORG_CUSTOMER_ID = 1L;

  private static final long INTEGRATION_ID = 20L;

  private static final String TOKEN = RandomStringUtils.randomAlphanumeric(25);

  @Mock
  private SubscriptionInfoClient mockSubscriptionInfoClient;

  @Mock
  private OrgConfigRepository mockOrgConfigRepository;

  @Mock
  private PollerStatusRepository mockPollerStatusRepository;

  @InjectMocks
  private InstallService installService;

  private SubscriptionResponse<IntegrationInfo> subscriptionResponse;

  private static OrgIntegrationInfo.Item buildItem() {
    final OrgIntegrationInfo.Item item = new OrgIntegrationInfo.Item();
    item.setId(1L);
    item.setInstalledOn(LocalDateTime.now());
    item.setInstalledBy("Junit");
    item.setUrl("url");

    return item;
  }

  @BeforeEach
  public void setup() {

    subscriptionResponse = new SubscriptionResponse();
    subscriptionResponse.setResponse(new IntegrationInfo());
    subscriptionResponse.getResponse().setUrl("dummyUrl");
    doReturn(subscriptionResponse).when(mockSubscriptionInfoClient)
      .integrationInfo(any(Long.class));

    ReflectionTestUtils.setField(installService, "integrationId", INTEGRATION_ID);
  }

  @Test
  public void testGetSettingsConfig() {
    //Setup
    final Item item = buildItem();
    when(mockSubscriptionInfoClient.getInstalledSubscription(anyLong(), anyLong()))
      .thenReturn(buildSubscriptionResponse(item));
    final PollerStatus status = buildPollerStatus();
    status.setLastDateUpdated(item.getInstalledOn().plusDays(1));
    when(mockPollerStatusRepository.findById(anyLong())).thenReturn(Optional.of(status));
    final OrgConfig orgConfig = buildOrgConfig(true, true, true, new ImmediateConversationWindow());
    when(mockOrgConfigRepository.findById(anyLong()))
      .thenReturn(Optional.of(orgConfig));

    //Execute
    final SettingsConfig result = installService.getSettingsConfig(ORG_CUSTOMER_ID);

    //Validate
    verify(mockSubscriptionInfoClient, times(1))
      .getInstalledSubscription(ORG_CUSTOMER_ID, INTEGRATION_ID);
    verify(mockPollerStatusRepository, times(1)).findById(item.getId());
    verify(mockOrgConfigRepository, times(1)).findById(ORG_CUSTOMER_ID);
    verify(mockSubscriptionInfoClient, times(1)).integrationInfo(any(Long.class));

    final IntegrationStatus integrationStatus = result.getStatus();
    assertTrue(integrationStatus.getInstalled());
    assertEquals(integrationStatus.getInstalledBy(), item.getInstalledBy());
    assertEquals(integrationStatus.getInstalledOn(), item.getInstalledOn());
    assertEquals(integrationStatus.getSyncActive(), status.getEnabled());

    final Settings existingSettings = result.getSettings();
    assertEquals(item.getUrl(), existingSettings.getBaseUrl());
    assertEquals(orgConfig.getContactSyncFromCrmEnabled(),
      existingSettings.getContactSyncFromCrmEnabled());
    assertEquals(orgConfig.getMessageArchiveToCrmEnabled(),
      existingSettings.getMessageArchiveToCrmEnabled());
    assertEquals(orgConfig.getTextFromCrmEnabled(), existingSettings.getTextFromCrmEnabled());
    assertEquals(orgConfig.getConversationWindow().getQuantity(),
      existingSettings.getConversationWindow());
    assertEquals(TimezoneList.get(), result.getTimezoneList());
    assertEquals(orgConfig.getSyncParams().getTimezone(), existingSettings.getTimezone());
  }

  @Test
  public void testGetSettingsConfigNoInstalled() {
    //Setup
    when(mockSubscriptionInfoClient.getInstalledSubscription(anyLong(), anyLong()))
      .thenReturn(buildSubscriptionResponse(null));
    when(mockPollerStatusRepository.findById(anyLong()))
      .thenReturn(Optional.of(buildPollerStatus()));
    final OrgConfig orgConfig = buildOrgConfig(true, true, true, new ImmediateConversationWindow());
    when(mockOrgConfigRepository.findById(anyLong())).thenReturn(Optional.of(orgConfig));

    //Execute
    final SettingsConfig result = installService.getSettingsConfig(ORG_CUSTOMER_ID);

    //Validate
    verify(mockSubscriptionInfoClient).getInstalledSubscription(ORG_CUSTOMER_ID, INTEGRATION_ID);
    verifyZeroInteractions(mockPollerStatusRepository);
    verify(mockOrgConfigRepository).findById(ORG_CUSTOMER_ID);

    final IntegrationStatus integrationStatus = result.getStatus();
    assertFalse(integrationStatus.getInstalled());
    assertNull(integrationStatus.getInstalledBy());
    assertNull(integrationStatus.getInstalledOn());
    assertNull(result.getStatus().getSyncActive());

    final Settings existingSettings = result.getSettings();
    assertNull(existingSettings.getBaseUrl());
    assertEquals(orgConfig.getContactSyncFromCrmEnabled(),
      existingSettings.getContactSyncFromCrmEnabled());
    assertEquals(orgConfig.getMessageArchiveToCrmEnabled(),
      existingSettings.getMessageArchiveToCrmEnabled());
    assertEquals(orgConfig.getTextFromCrmEnabled(), existingSettings.getTextFromCrmEnabled());
    assertEquals(orgConfig.getConversationWindow().getQuantity(),
      existingSettings.getConversationWindow());
    assertEquals(orgConfig.getSyncParams().getTimezone(), existingSettings.getTimezone());
    assertEquals(TimezoneList.get(), result.getTimezoneList());
  }

  @Test
  public void testGetSettingsConfigNoOrgConfig() {
    //Setup
    when(mockSubscriptionInfoClient.getInstalledSubscription(anyLong(), anyLong()))
      .thenReturn(buildSubscriptionResponse(buildItem()));
    doThrow(new NoOrgConfigException(ORG_CUSTOMER_ID, INTEGRATION_ID)).when(mockOrgConfigRepository)
      .findById(anyLong());

    //Execute & Validate
    assertThrows(NoOrgConfigException.class,
      () -> installService.getSettingsConfig(ORG_CUSTOMER_ID));
  }

  @Test
  public void testSaveSettings() {
    //Setup
    final OrgConfig existingConfig = buildOrgConfig(false, false, false,
      buildIdleTimeConversationWindow());
    final SettingsConfig newConfig = buildSettingViewConfig(true, true, "url", 4);
    when(mockOrgConfigRepository.findById(anyLong()))
      .thenReturn(Optional.of(existingConfig));

    //Execute
    installService.saveSettings(ORG_CUSTOMER_ID, newConfig);

    //Validate
    final Settings existingSettings = newConfig.getSettings();
    assertEquals(existingConfig.getSyncParams().getTimezone(), existingSettings.getTimezone());
    assertEquals(existingConfig.getContactSyncFromCrmEnabled(),
      existingSettings.getContactSyncFromCrmEnabled());
    assertEquals(existingConfig.getMessageArchiveToCrmEnabled(),
      existingSettings.getMessageArchiveToCrmEnabled());
    assertEquals(existingConfig.getTextFromCrmEnabled(), existingSettings.getTextFromCrmEnabled());
    assertEquals(existingConfig.getConversationWindow().getQuantity(),
      existingSettings.getConversationWindow());
    assertEquals(existingConfig.getSyncParams().getTimezone(), existingSettings.getTimezone());
  }

  @Test
  public void testSaveSettingNoOrgConfig() {
    //Setup
    doThrow(new NoOrgConfigException(ORG_CUSTOMER_ID, INTEGRATION_ID))
      .when(mockOrgConfigRepository).findById(anyLong());

    //Execute & Validate
    assertThrows(NoOrgConfigException.class, () -> installService.saveSettings(ORG_CUSTOMER_ID,
      buildSettingViewConfig(true, true, "url", 4)));
  }

  private SubscriptionResponse<Item> buildSubscriptionResponse(final Item item) {
    final SubscriptionResponse<Item> response = new SubscriptionResponse<>();
    response.setSuccess(true);
    response.setResponse(item);

    return response;
  }

  private OrgConfig buildOrgConfig(final boolean contactSyncEnabled,
    final boolean messageArchiveEnabled,
    final boolean textFromCrmEnabled, final ConversationStrategy conversationWindow) {
    final OrgConfig orgConfig = new OrgConfig();
    orgConfig.setOrgCustomerId(ORG_CUSTOMER_ID);
    orgConfig.setZipwhipToken("Bearer " + TOKEN);
    orgConfig.setSyncParams(SyncParams.DEFAULT);
    orgConfig.setLocale("en_US");
    orgConfig.setContactSyncFromCrmEnabled(contactSyncEnabled);
    orgConfig.setMessageArchiveToCrmEnabled(messageArchiveEnabled);
    orgConfig.setTextFromCrmEnabled(textFromCrmEnabled);
    orgConfig.setConversationWindow(conversationWindow);

    return orgConfig;
  }

  private PollerStatus buildPollerStatus() {
    final PollerStatus pollerStatus = new PollerStatus();
    pollerStatus.setEnabled(true);
    pollerStatus.setOrgId(ORG_CUSTOMER_ID);
    pollerStatus.setIntegrationId(INTEGRATION_ID);

    return pollerStatus;
  }

  private IdleTimeConversationWindow buildIdleTimeConversationWindow() {
    final IdleTimeConversationWindow conversationWindow = new IdleTimeConversationWindow();
    conversationWindow.setQuantity(4);
    conversationWindow.setUnitOfTime(UnitOfTime.H);

    return conversationWindow;
  }

  private SettingsConfig buildSettingViewConfig(final Boolean installed, final boolean isSyncActive,
    final String baseUrl, final int period) {

    final SettingsConfig viewConfig = new SettingsConfig();
    viewConfig.setTimezoneList(TimezoneList.get());
    final IntegrationStatus status = viewConfig.getStatus();
    status.setInstalled(installed);
    status.setInstalledOn(LocalDateTime.now());
    status.setInstalledBy("Junit");
    status.setSyncActive(isSyncActive);
    final Settings existingSettings = viewConfig.getSettings();
    existingSettings.setBaseUrl(baseUrl);
    existingSettings.setTextFromCrmEnabled(true);
    existingSettings.setMessageArchiveToCrmEnabled(true);
    existingSettings.setContactSyncFromCrmEnabled(true);
    existingSettings.setConversationWindow(period);
    existingSettings.setTimezone("America/Los_Angeles");

    return viewConfig;
  }

}