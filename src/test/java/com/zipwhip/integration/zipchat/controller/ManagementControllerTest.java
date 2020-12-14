package com.zipwhip.integration.zipchat.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.zipwhip.integration.zipchat.client.ClaimAPIClient;
import com.zipwhip.integration.zipchat.domain.SyncTargets;
import com.zipwhip.integration.zipchat.domain.SyncableMessage;
import com.zipwhip.integration.zipchat.entities.OrgConfig;
import com.zipwhip.integration.zipchat.entities.StoredNote;
import com.zipwhip.integration.zipchat.poller.DatahubPoller;
import com.zipwhip.integration.zipchat.repository.OrgConfigRepository;
import com.zipwhip.integration.zipchat.service.SyncServiceImpl;
import com.zipwhip.integration.test.junit.logging.LoggingExtension;
import com.zipwhip.message.domain.InboundContact;
import com.zipwhip.message.domain.InboundMessage;

import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@ExtendWith(LoggingExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class ManagementControllerTest {

  private final static String MAIN_API = "/management";

  private final static String JUNIT_MESSAGE_EXCEPTION = "JUNIT EXCEPTION TEST";

  private final static long INTEGRATION_ID = 22;

  @Mock
  private OrgConfigRepository mockConfigRepo;

  @Mock
  private DatahubPoller mockPoller;

  @Mock
  private SyncServiceImpl mockSyncService;

  @InjectMocks
  private ManagementController controller;

  private MockMvc mockMvc;

  private OrgConfig orgConfig;

  @BeforeEach
  public void setup() {

    mockMvc = MockMvcBuilders
      .standaloneSetup(controller)
      .build();

    orgConfig = new OrgConfig();
    injectableMessage = new InboundMessage();
    injectableContact = new InboundContact();
    doReturn(Optional.of(orgConfig)).when(mockConfigRepo).findById(eq(187207l));
  }

  @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
  @Test
  public void testGetOrgConfig() throws Exception {
    //Setup
    long orgCustomerId = 187207l;
    long integrationId = 14l;
    orgConfig.setOrgCustomerId(orgCustomerId);

    //Execute
    String apiCall = "/config/" + orgCustomerId;
    String url = MAIN_API + apiCall;
    MvcResult result = this.mockMvc.perform(get(url)
      .param("integrationId", Long.toString(integrationId))
      .contentType(MediaType.APPLICATION_JSON)).andDo(print())

      //Validate
      .andExpect(status().is(HttpServletResponse.SC_OK))
      .andExpect(content().json(convertToJson(orgConfig)))
      .andReturn();

    verify(mockConfigRepo, times(1)).findById(nullable(Long.class));

  }

  @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
  @Test
  public void testGetOrgConfigNotFound() throws Exception {

    //Setup
    doReturn(Optional.ofNullable(null)).when(mockConfigRepo).findById(eq(187207l));
    long orgCustomerId = 187207l;
    long integrationId = 14l;

    //Execute
    String apiCall = "/config/" + orgCustomerId;
    String url = MAIN_API + apiCall;
    MvcResult result = this.mockMvc.perform(get(url)
      .param("integrationId", Long.toString(integrationId))
      .contentType(MediaType.APPLICATION_JSON)).andDo(print())

      //Validate
      .andExpect(status().is(HttpServletResponse.SC_OK))
      .andExpect(content().string(""))
      .andReturn();

    verify(mockConfigRepo, times(1)).findById(nullable(Long.class));
  }

  @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
  @Test
  public void testUpsertOrgConfigNew() throws Exception {
    //Setup
    doReturn(Optional.ofNullable(null)).when(mockConfigRepo).findById(eq(187207l));
    long orgCustomerId = 187207l;
    long integrationId = 14l;
    orgConfig.setOrgCustomerId(orgCustomerId);

    //Execute
    String apiCall = "/config/" + orgCustomerId;
    String url = MAIN_API + apiCall;
    MvcResult result = this.mockMvc.perform(put(url)
      .param("integrationId", Long.toString(integrationId))
      .content(convertToJson(orgConfig))
      .contentType(MediaType.APPLICATION_JSON)).andDo(print())

      //Validate
      .andExpect(status().is(HttpServletResponse.SC_OK))
      .andExpect(content().string("success"))
      .andReturn();

    verify(mockConfigRepo, times(1)).findById(nullable(Long.class));
    verify(mockConfigRepo, times(1)).save(eq(orgConfig));

  }

  @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
  @Test
  public void testUpsertOrgConfigUpdateWithMismatch() throws Exception {

    //Setup
    long orgCustomerId = 187207l;
    long badOrgCustomerId = orgCustomerId + 1;
    long integrationId = 14l;
    orgConfig.setOrgCustomerId(orgCustomerId);

    //Execute
    String apiCall = "/config/" + badOrgCustomerId;
    String url = MAIN_API + apiCall;
    MvcResult result = this.mockMvc.perform(put(url)
      .param("integrationId", Long.toString(integrationId))
      .content(convertToJson(orgConfig))
      .contentType(MediaType.APPLICATION_JSON)).andDo(print())

      //Validate
      .andExpect(status().is(HttpServletResponse.SC_BAD_REQUEST))
      .andExpect(content().string("Config doesn't match provided orgCustomerId"))
      .andReturn();

    verify(mockConfigRepo, times(0)).findById(nullable(Long.class));
    verify(mockConfigRepo, times(0)).save(eq(orgConfig));

  }

  @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
  @Test
  public void testDeleteOrgConfig() throws Exception {

    //Setup
    doReturn(Optional.ofNullable(orgConfig)).when(mockConfigRepo).findById(eq(187207l));
    long orgCustomerId = 187207l;
    long integrationId = 14l;
    orgConfig.setOrgCustomerId(orgCustomerId);

    //Execute
    String apiCall = "/config/" + orgCustomerId;
    String url = MAIN_API + apiCall;
    MvcResult result = this.mockMvc.perform(delete(url)
      .param("integrationId", Long.toString(integrationId))
      .content(convertToJson(orgConfig))
      .contentType(MediaType.APPLICATION_JSON)).andDo(print())

      //Validate
      .andExpect(status().is(HttpServletResponse.SC_OK))
      .andExpect(content().json(convertToJson(orgConfig)))
      .andReturn();

    verify(mockConfigRepo, times(1)).findById(nullable(Long.class));
    verify(mockConfigRepo, times(1)).delete(eq(orgConfig));

  }

  @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
  @Test
  public void testDeleteOrgConfigNotFound() throws Exception {

    //Setup
    doReturn(Optional.ofNullable(null)).when(mockConfigRepo).findById(eq(187207l));
    long orgCustomerId = 187207l;
    long integrationId = 14l;
    orgConfig.setOrgCustomerId(orgCustomerId);

    //Execute
    String apiCall = "/config/" + orgCustomerId;
    String url = MAIN_API + apiCall;
    MvcResult result = this.mockMvc.perform(delete(url)
      .param("integrationId", Long.toString(integrationId))
      .content(convertToJson(orgConfig))
      .contentType(MediaType.APPLICATION_JSON)).andDo(print())

      //Validate
      .andExpect(status().is(HttpServletResponse.SC_OK))
      .andExpect(content().string(""))
      .andReturn();

    verify(mockConfigRepo, times(1)).findById(nullable(Long.class));
    verify(mockConfigRepo, times(0)).delete(eq(orgConfig));
  }

  public static String convertToJson(Object object) throws Exception {
    return convertToJson(object, false);
  }

  public static String convertToJson(Object object, boolean wrapRoot) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, wrapRoot);
    ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
    return objectWriter.writeValueAsString(object);
  }
}