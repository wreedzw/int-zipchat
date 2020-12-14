package com.zipwhip.integration.zipchat.install;

import static com.zipwhip.integration.test.junit.logging.LoggingExtension.assertLogged;
import static com.zipwhip.integration.test.junit.logging.LoggingExtension.assertNotLogged;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipwhip.integration.zipchat.domain.IntegrationStatus;
import com.zipwhip.integration.zipchat.domain.Settings;
import com.zipwhip.integration.zipchat.domain.SettingsConfig;
import com.zipwhip.integration.zipchat.entities.OrgConfig;
import com.zipwhip.integration.zipchat.repository.OrgConfigRepository;
import com.zipwhip.integration.zipchat.service.InstallService;
import com.zipwhip.integration.test.junit.logging.LoggingExtension;
import com.zipwhip.kafka.poller.status.domain.PollerStatus;
import com.zipwhip.kafka.poller.status.repository.PollerStatusRepository;
import com.zipwhip.legacy.config.AccountConsoleConfig;
import com.zipwhip.smssync.domain.TimezoneList;
import com.zipwhip.subscription.client.SubscriptionInfoClient;
import com.zipwhip.subscription.domain.IntegrationInfo;
import com.zipwhip.subscription.domain.OrgIntegrationInfo;
import com.zipwhip.subscription.domain.SubscriptionPayload;
import com.zipwhip.subscription.domain.SubscriptionResponse;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

@ExtendWith(MockitoExtension.class)
@ExtendWith(LoggingExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class InstallControllerTest {


  private final static String MAIN_API = "/secure/install";

  @Mock
  private SubscriptionInfoClient mockSubscriptionInfoClient;

  @Mock
  private OrgConfigRepository mockOrgConfigRepository;

  @Mock
  private PollerStatusRepository mockPollerStatusRepository;

  @Mock
  private AccountConsoleConfig mockAccountConsoleConfig;

  @Mock
  private ZipwhipAcceleratorAPIClient mockApiClient;

  @Mock
  private InstallService mockInstallService;

  @InjectMocks
  private InstallController controller;

  private MockMvc mockMvc;

  private SubscriptionResponse<OrgIntegrationInfo.Item> subResponse;

  private PollerStatus pollerStatus;

  private SubscriptionResponse<Long> installPayload;

  @BeforeEach
  public void setup() {

    mockMvc = MockMvcBuilders
      .standaloneSetup(controller)
      .build();

    controller.setIntegrationId(20l);
    installPayload = new SubscriptionResponse<>();
    doReturn(installPayload).when(mockSubscriptionInfoClient)
      .createOrUpdateSubscription(any(SubscriptionPayload.class));
    subResponse = new SubscriptionResponse<>();
    doReturn(subResponse).when(mockSubscriptionInfoClient)
      .getInstalledSubscription(nullable(Long.class), nullable(Long.class));
    doReturn(Optional.ofNullable(pollerStatus)).when(mockPollerStatusRepository)
      .findById(any(Long.class));
  }

  @Test
  public void testGetSettingsNotInstalled() throws Exception {

    //Setup
    long orgCustomerId = 187207l;
    final SettingsConfig viewConfig = buildSettingViewConfig(false, true, null);
    when(mockInstallService.getSettingsConfig(anyLong())).thenReturn(viewConfig);

    //Execute
    String apiCall = "/settings/orgId/" + orgCustomerId;
    String url = MAIN_API + apiCall;
    MvcResult result = this.mockMvc.perform(get(url)
      .contentType(MediaType.APPLICATION_JSON)).andDo(print())

      //Validate
      .andExpect(status().is(HttpServletResponse.SC_OK))
      .andExpect(model().attribute("params", viewConfig))
      .andReturn();

  }

  @Test
  public void testGetSettingsInstalledNoSync() throws Exception {

    //Setup
    long orgCustomerId = 187207l;
    OrgIntegrationInfo.Item item = new OrgIntegrationInfo.Item();
    subResponse.setResponse(item);
    doReturn(Optional.ofNullable(null)).when(mockPollerStatusRepository).findById(any(Long.class));
    final SettingsConfig viewConfig = buildSettingViewConfig(true, false, "baseUrl");
    when(mockInstallService.getSettingsConfig(anyLong())).thenReturn(viewConfig);

    //Execute
    String apiCall = "/settings/orgId/" + orgCustomerId;
    String url = MAIN_API + apiCall;
    MvcResult result = this.mockMvc.perform(get(url)
      .contentType(MediaType.APPLICATION_JSON)).andDo(print())

      //Validate
      .andExpect(status().is(HttpServletResponse.SC_OK))
      .andExpect(model().attribute("params", viewConfig))
      .andReturn();

  }

  @Test
  public void testGetSettingsInstalledWithSync() throws Exception {

    //Setup
    long orgCustomerId = 187207l;
    OrgIntegrationInfo.Item item = new OrgIntegrationInfo.Item();
    subResponse.setResponse(item);
    final SettingsConfig viewConfig = buildSettingViewConfig(true, true, "baseUrl");
    when(mockInstallService.getSettingsConfig(anyLong())).thenReturn(viewConfig);

    //Execute
    String apiCall = "/settings/orgId/" + orgCustomerId;
    String url = MAIN_API + apiCall;
    MvcResult result = this.mockMvc.perform(get(url)
      .contentType(MediaType.APPLICATION_JSON)).andDo(print())

      //Validate
      .andExpect(status().is(HttpServletResponse.SC_OK))
      .andExpect(model().attribute("params", viewConfig))
      .andReturn();

  }

  @Test
  public void testGetDetails() throws Exception {

    //Setup
    long orgCustomerId = 187207l;

    //Execute
    String apiCall = "/details/orgId/" + orgCustomerId;
    String url = MAIN_API + apiCall;
    MvcResult result = this.mockMvc.perform(get(url)
      .contentType(MediaType.APPLICATION_JSON)).andDo(print())

      //Validate
      .andExpect(status().is(HttpServletResponse.SC_OK))
      .andExpect(model().attribute("orgId", orgCustomerId))
      .andReturn();

  }

  @Test
  public void testInstallSuccessful() throws Exception {

    //Setup
    long orgCustomerId = 187207l;
    installPayload.setSuccess(true);

    //Execute
    String apiCall = "/orgId/" + orgCustomerId;
    String url = MAIN_API + apiCall;
    MvcResult result = this.mockMvc.perform(post(url)
      .contentType(MediaType.APPLICATION_JSON)).andDo(print())

      //Validate
      .andExpect(status().is(HttpServletResponse.SC_OK))
      .andExpect(forwardedUrl("//secure/install/authenticate/orgId/187207?reconnect=false"))
      .andReturn();

  }

  @Test
  public void testInstallFailure() throws Exception {

    //Setup
    long orgCustomerId = 187207l;
    installPayload.setSuccess(false);

    //Execute
    String apiCall = "/orgId/" + orgCustomerId;
    String url = MAIN_API + apiCall;
    MvcResult result = this.mockMvc.perform(post(url)
      .contentType(MediaType.APPLICATION_JSON)).andDo(print())

      //Validate
      .andExpect(status().is(HttpServletResponse.SC_OK))
      .andExpect(forwardedUrl("//secure/install/authenticate/orgId/187207?reconnect=false"))
      .andReturn();

  }

  @Test
  public void testDetails() throws Exception {

    //Setup
    long orgCustomerId = 187207l;
    installPayload.setSuccess(false);

    //Execute
    String apiCall = "/details/orgId/" + orgCustomerId;
    String url = MAIN_API + apiCall;
    MvcResult result = this.mockMvc.perform(get(url)
      .contentType(MediaType.APPLICATION_JSON)).andDo(print())

      //Validate
      .andExpect(status().is(HttpServletResponse.SC_OK))
      .andExpect(view().name("details"))
      .andExpect(model().attribute("orgId", orgCustomerId))
      .andReturn();

  }

  @Test
  public void testAuthenticate() throws Exception {

    //Setup
    long orgCustomerId = 187207l;
    long integrationId = 20l;
    boolean reconnect = false;
    installPayload.setSuccess(false);
    SubscriptionResponse<IntegrationInfo> record = new SubscriptionResponse<>();
    record.setResponse(new IntegrationInfo());
    record.setSuccess(true);
    doReturn(record).when(mockSubscriptionInfoClient).integrationInfo(any(Long.class));

    //Execute
    String apiCall = "/authenticate/orgId/" + orgCustomerId;
    String url = MAIN_API + apiCall;
    MvcResult result = this.mockMvc.perform(get(url)
      .contentType(MediaType.APPLICATION_JSON)).andDo(print())

      //Validate
      .andExpect(status().is(HttpServletResponse.SC_OK))
      .andExpect(view().name("authenticate"))
      .andExpect(model().attribute("reconnect", reconnect))
      .andExpect(model().attribute("integrationId", integrationId))
      .andExpect(model().attribute("info", record.getResponse()))
      .andReturn();

  }

  @Test
  public void testConnect() throws Exception {
    //Setup
    long orgCustomerId = 187207l;
    installPayload.setSuccess(false);
    String installBy = "InstallerName";
    long integrationId = 20l;
    boolean reconnect = false;
    SubscriptionPayload expectedSubscriptionPayload = new SubscriptionPayload();
    expectedSubscriptionPayload.setInstalledBy(installBy);
    expectedSubscriptionPayload.setIntegrationId(integrationId);
    expectedSubscriptionPayload.setOrgCustomerId(orgCustomerId);
    SubscriptionResponse<Long> subResponse = new SubscriptionResponse<>();
    subResponse.setSuccess(true);
    SubscriptionResponse<Long> r = new SubscriptionResponse<>();
    r.setSuccess(true);
    doReturn(subResponse).when(mockSubscriptionInfoClient)
      .createOrUpdateSubscription(any(SubscriptionPayload.class));
    String expectedRedirectUrl =
      "/int-zipchat-service/secure/install/settings/orgId/" + orgCustomerId;
    doReturn(true).when(mockApiClient).verifyAccess(any(OrgConfig.class));
    AuthenticationCredentials authCredentials = new AuthenticationCredentials();
    authCredentials.setUsername("unam");
    authCredentials.setPassword("pw");
    authCredentials.setGwBaseUrl("localhost:1234");

    //Execute
    String apiCall = "/connect/" + orgCustomerId;
    String url = MAIN_API + apiCall;
    MvcResult result = this.mockMvc.perform(post(url)
      .param("installedBy", installBy)
      .param("reconnect", "false")
      .flashAttr("credentials", authCredentials)
      .contentType(MediaType.APPLICATION_JSON)).andDo(print())

      //Validate
      .andExpect(status().is(HttpServletResponse.SC_OK))
      .andReturn();

    //Execute
    assertNotLogged("No credentials provided");
    assertNotLogged("Unable to verify credentials. Org Id");
    verify(mockOrgConfigRepository).save(any(OrgConfig.class));
    verify(mockAccountConsoleConfig)
      .redirectBack(any(HttpServletRequest.class), any(HttpServletResponse.class), any(Long.class),
        eq(expectedRedirectUrl), any(String.class));

  }

  @Test
  public void testConnectReconnect() throws Exception {
    //Setup
    long orgCustomerId = 187207l;
    installPayload.setSuccess(false);
    String installBy = "InstallerName";
    long integrationId = 20l;
    boolean reconnect = false;
    SubscriptionPayload expectedSubscriptionPayload = new SubscriptionPayload();
    expectedSubscriptionPayload.setInstalledBy(installBy);
    expectedSubscriptionPayload.setIntegrationId(integrationId);
    expectedSubscriptionPayload.setOrgCustomerId(orgCustomerId);
    SubscriptionResponse<Long> subResponse = new SubscriptionResponse<>();
    subResponse.setSuccess(true);
    SubscriptionResponse<Long> r = new SubscriptionResponse<>();
    r.setSuccess(true);
    doReturn(subResponse).when(mockSubscriptionInfoClient)
      .createOrUpdateSubscription(any(SubscriptionPayload.class));
    String expectedRedirectUrl = "/int-zipchat-service/";
    doReturn(true).when(mockApiClient).verifyAccess(any(OrgConfig.class));
    AuthenticationCredentials authCredentials = new AuthenticationCredentials();
    authCredentials.setUsername("unam");
    authCredentials.setPassword("pw");
    authCredentials.setGwBaseUrl("localhost:1234");
    OrgConfig orgConfig = new OrgConfig();
    doReturn(Optional.of(orgConfig)).when(mockOrgConfigRepository).findById(orgCustomerId);

    //Execute
    String apiCall = "/connect/" + orgCustomerId;
    String url = MAIN_API + apiCall;
    MvcResult result = this.mockMvc.perform(post(url)
      .param("installedBy", installBy)
      .param("reconnect", "true")
      .flashAttr("credentials", authCredentials)
      .contentType(MediaType.APPLICATION_JSON)).andDo(print())

      //Validate
      .andExpect(status().is(HttpServletResponse.SC_OK))
      .andReturn();

    //Execute
    assertNotLogged("No credentials provided");
    assertNotLogged("Unable to verify credentials. Org Id");
    verify(mockOrgConfigRepository).save(any(OrgConfig.class));
    verify(mockAccountConsoleConfig)
      .redirectBack(any(HttpServletRequest.class), any(HttpServletResponse.class), any(Long.class),
        eq(expectedRedirectUrl), any(String.class), eq(true));

  }

  @Test
  public void testConnectFailureToVerify() throws Exception {
    //Setup
    long orgCustomerId = 187207l;
    installPayload.setSuccess(false);
    String installBy = "InstallerName";
    long integrationId = 20l;
    boolean reconnect = false;
    SubscriptionPayload expectedSubscriptionPayload = new SubscriptionPayload();
    expectedSubscriptionPayload.setInstalledBy(installBy);
    expectedSubscriptionPayload.setIntegrationId(integrationId);
    expectedSubscriptionPayload.setOrgCustomerId(orgCustomerId);
    SubscriptionResponse<Long> subResponse = new SubscriptionResponse<>();
    subResponse.setSuccess(true);
    SubscriptionResponse<Long> r = new SubscriptionResponse<>();
    r.setSuccess(true);
    doReturn(subResponse).when(mockSubscriptionInfoClient)
      .createOrUpdateSubscription(any(SubscriptionPayload.class));
    String expectedRedirectUrl = "/int-zipchat-service/";
    doReturn(true).when(mockApiClient).verifyAccess(any(OrgConfig.class));
    AuthenticationCredentials authCredentials = new AuthenticationCredentials();
    authCredentials.setUsername("unam");
    authCredentials.setPassword("pw");
    authCredentials.setGwBaseUrl("localhost:1234");
    OrgConfig orgConfig = new OrgConfig();
    doReturn(Optional.of(orgConfig)).when(mockOrgConfigRepository).findById(orgCustomerId);
    doThrow(new RuntimeException("JUNIT_EXCEPTION")).when(mockApiClient)
      .verifyAccess(any(OrgConfig.class));

    //Execute
    String apiCall = "/connect/" + orgCustomerId;
    String url = MAIN_API + apiCall;
    MvcResult result = this.mockMvc.perform(post(url)
      .param("installedBy", installBy)
      .param("reconnect", "true")
      .flashAttr("credentials", authCredentials)
      .contentType(MediaType.APPLICATION_JSON)).andDo(print())

      //Validate
      .andExpect(status().is(HttpServletResponse.SC_OK))
      .andReturn();

    //Execute
    assertNotLogged("No credentials provided");
    assertLogged("Unable to verify credentials. Org Id");
    verify(mockOrgConfigRepository, never()).save(any(OrgConfig.class));
    ArgumentCaptor<String> redirectCaptor = ArgumentCaptor.forClass(String.class);
    verify(mockAccountConsoleConfig)
      .redirectBack(any(HttpServletRequest.class), any(HttpServletResponse.class), any(Long.class),
        redirectCaptor.capture(), eq(null));
    assertTrue(redirectCaptor.getValue().contains("error=Invalid Credentials"));

  }

  @Test
  public void testReConnect() throws Exception {
    //Setup
    long orgCustomerId = 187207l;
    installPayload.setSuccess(false);
    String installBy = "InstallerName";
    long integrationId = 20l;
    boolean reconnect = true;
    SubscriptionPayload expectedSubscriptionPayload = new SubscriptionPayload();
    expectedSubscriptionPayload.setInstalledBy(installBy);
    expectedSubscriptionPayload.setIntegrationId(integrationId);
    expectedSubscriptionPayload.setOrgCustomerId(orgCustomerId);
    SubscriptionResponse<Long> subResponse = new SubscriptionResponse<>();
    subResponse.setSuccess(true);
    SubscriptionResponse<Long> r = new SubscriptionResponse<>();
    r.setSuccess(true);
    doReturn(subResponse).when(mockSubscriptionInfoClient)
      .createOrUpdateSubscription(any(SubscriptionPayload.class));
    String expectedRedirectUrl =
      "/int-zipchat-service/secure/install/settings/orgId/" + orgCustomerId;
    doReturn(true).when(mockApiClient).verifyAccess(any(OrgConfig.class));
    AuthenticationCredentials authCredentials = new AuthenticationCredentials();
    authCredentials.setUsername("unam");
    authCredentials.setPassword("pw");
    authCredentials.setGwBaseUrl("localhost:1234");

    //Execute
    String apiCall = "/connect/" + orgCustomerId;
    String url = MAIN_API + apiCall;
    MvcResult result = this.mockMvc.perform(post(url)
      .param("installedBy", installBy)
      .param("reconnect", "false")
      .flashAttr("credentials", authCredentials)
      .contentType(MediaType.APPLICATION_JSON)).andDo(print())

      //Validate
      .andExpect(status().is(HttpServletResponse.SC_OK))
      .andReturn();

    //Execute
    assertNotLogged("No credentials provided");
    assertNotLogged("Unable to verify credentials. Org Id");
    verify(mockOrgConfigRepository).save(any(OrgConfig.class));
    verify(mockAccountConsoleConfig)
      .redirectBack(any(HttpServletRequest.class), any(HttpServletResponse.class), any(Long.class),
        eq(expectedRedirectUrl), any(String.class));

  }

  @Test
  public void testSaveSettings() throws Exception {

    //Setup
    final String orgCustomerId = "187207";
    final String apiCall = "/settings/orgId/" + orgCustomerId;
    final String url = MAIN_API + apiCall;
    doNothing().when(mockInstallService).saveSettings(anyLong(), any(SettingsConfig.class));
    final OrgConfig orgConfig = new OrgConfig();

    //Execute
    this.mockMvc.perform(post(url)
      .content(new ObjectMapper().writeValueAsString(orgConfig))
      .param("orgCustomerId", orgCustomerId)
      .contentType(MediaType.APPLICATION_JSON)
    ).andDo(print())

      //Validate
      .andExpect(status().is(HttpServletResponse.SC_FOUND))
      .andExpect(redirectedUrl("/secure/install/settings/orgId/" + orgCustomerId + "?saved"))
      .andReturn();

  }

  @Test
  public void testSaveSettingsNoOrgConfig() throws Exception {

    //Setup
    final Long orgCustomerId = 187207L;
    final long integrationId = 20;
    final String apiCall = "/settings/orgId/" + orgCustomerId;
    final String url = MAIN_API + apiCall;
    doThrow(new NoOrgConfigException(orgCustomerId, integrationId))
      .when(mockInstallService).saveSettings(anyLong(), any(SettingsConfig.class));
    final OrgConfig orgConfig = new OrgConfig();

    //Execute
    this.mockMvc.perform(post(url)
      .content(new ObjectMapper().writeValueAsString(orgConfig))
      .param("orgCustomerId", orgCustomerId.toString())
      .contentType(MediaType.APPLICATION_JSON)
    ).andDo(print())

      //Validate
      .andExpect(status().is(HttpServletResponse.SC_FOUND))
      .andExpect(
        redirectedUrl("/secure/install/settings/orgId/" + orgCustomerId + "?error=NoOrgConfig"))
      .andReturn();

  }

  @Test
  public void testSaveSettingsException() throws Exception {

    //Setup
    final Long orgCustomerId = 187207L;
    final String apiCall = "/settings/orgId/" + orgCustomerId;
    final String url = MAIN_API + apiCall;
    doThrow(new RuntimeException("Junit exception")).when(mockInstallService)
      .saveSettings(anyLong(), any(SettingsConfig.class));
    final OrgConfig orgConfig = new OrgConfig();

    //Execute
    this.mockMvc.perform(post(url)
      .content(new ObjectMapper().writeValueAsString(orgConfig))
      .param("orgCustomerId", orgCustomerId.toString())
      .contentType(MediaType.APPLICATION_JSON)
    ).andDo(print())

      //Validate
      .andExpect(status().is(HttpServletResponse.SC_FOUND))
      .andExpect(
        redirectedUrl("/secure/install/settings/orgId/" + orgCustomerId + "?error=Junit exception"))
      .andReturn();

  }

  private SettingsConfig buildSettingViewConfig(final Boolean installed, final boolean isSyncActive,
    final String baseUrl) {
    final SettingsConfig viewConfig = new SettingsConfig();
    viewConfig.setTimezoneList(TimezoneList.get());

    final IntegrationStatus integrationStatus = viewConfig.getStatus();
    integrationStatus.setInstalled(installed);
    integrationStatus.setInstalledOn(LocalDateTime.now());
    integrationStatus.setInstalledBy("Junit");
    integrationStatus.setSyncActive(isSyncActive);

    final Settings existingSettings = viewConfig.getSettings();
    existingSettings.setBaseUrl(baseUrl);
    existingSettings.setTimezone("America/Los_Angeles");
    existingSettings.setTextFromCrmEnabled(true);
    existingSettings.setMessageArchiveToCrmEnabled(true);
    existingSettings.setContactSyncFromCrmEnabled(true);
    existingSettings.setConversationWindow(4);

    return viewConfig;
  }

  @Test
  public void testTestAuthSuccess() throws Exception {
    //Setup
    long orgCustomerId = 187207l;
    doReturn(true).when(mockApiClient).verifyAccess(any(OrgConfig.class));
    OrgConfig orgConfig = new OrgConfig();
    doReturn(Optional.of(orgConfig)).when(mockOrgConfigRepository).findById(orgCustomerId);

    //Execute
    String apiCall = "/test_auth/orgId/" + orgCustomerId;
    String url = MAIN_API + apiCall;
    this.mockMvc.perform(get(url)
      .contentType(MediaType.APPLICATION_JSON)).andDo(print())

      //Validate
      .andExpect(status().is(HttpServletResponse.SC_OK));

    assertLogged("Successfully validated access");
  }

  @Test
  public void testTestAuthAccessDenied() throws Exception {
    //Setup
    long orgCustomerId = 187207l;
    doReturn(false).when(mockApiClient).verifyAccess(any(OrgConfig.class));
    OrgConfig orgConfig = new OrgConfig();
    doReturn(Optional.of(orgConfig)).when(mockOrgConfigRepository).findById(orgCustomerId);

    //Execute
    String apiCall = "/test_auth/orgId/" + orgCustomerId;
    String url = MAIN_API + apiCall;
    try {
      this.mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON)).andDo(print());
    } catch (NestedServletException e) {
      //Validate
      assertTrue(e.getCause() instanceof RuntimeException);
    }

    assertNotLogged("Successfully validated access");
    assertLogged("Unsuccessfully validated access");
    assertLogged("Failed testing access: ");
  }

  @Test
  public void testTestAuthFailed() throws Exception {
    //Setup
    long orgCustomerId = 187207l;
    doThrow(new RuntimeException()).when(mockApiClient).verifyAccess(any(OrgConfig.class));
    OrgConfig orgConfig = new OrgConfig();
    doReturn(Optional.of(orgConfig)).when(mockOrgConfigRepository).findById(orgCustomerId);

    //Execute
    String apiCall = "/test_auth/orgId/" + orgCustomerId;
    String url = MAIN_API + apiCall;
    try {
      this.mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON)).andDo(print());
    } catch (NestedServletException e) {
      //Validate
      assertTrue(e.getCause() instanceof RuntimeException);
    }

    assertNotLogged("Successfully validated access");
    assertNotLogged("Unsuccessfully validated access");
    assertLogged("Failed testing access: ");
  }

}

