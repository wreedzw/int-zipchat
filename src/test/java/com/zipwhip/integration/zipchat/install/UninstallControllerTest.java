package com.zipwhip.integration.zipchat.install;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zipwhip.subscription.client.SubscriptionInfoClient;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class UninstallControllerTest {


  private final static String MAIN_API = "/secure/uninstall";

  @Mock
  private SubscriptionInfoClient mockSubscriptionInfoClient;

  @InjectMocks
  private UninstallController controller;

  private MockMvc mockMvc;

  @BeforeEach
  public void setup() {

    mockMvc = MockMvcBuilders
      .standaloneSetup(controller)
      .build();

  }

  @Test
  public void testUninstall() throws Exception {

    //Setup
    long orgCustomerId = 187207l;

    //Execute
    String apiCall = "/orgId/" + orgCustomerId;
    String url = MAIN_API + apiCall;
    MvcResult result = this.mockMvc.perform(post(url)
      .contentType(MediaType.APPLICATION_JSON)).andDo(print())

      //Validate
      .andExpect(status().is(HttpServletResponse.SC_MOVED_TEMPORARILY))
      .andExpect(redirectedUrl("/secure/install/details/orgId/" + orgCustomerId))
      .andReturn();

    verify(mockSubscriptionInfoClient, times(1)).uninstall(eq(orgCustomerId), nullable(Long.class));
  }
}
