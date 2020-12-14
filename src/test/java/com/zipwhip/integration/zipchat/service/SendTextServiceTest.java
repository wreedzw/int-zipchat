package com.zipwhip.integration.zipchat.service;

import static com.zipwhip.integration.test.junit.logging.LoggingExtension.assertLogged;
import static com.zipwhip.integration.test.junit.logging.LoggingExtension.assertNotLogged;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.zipwhip.integration.bi.service.StatsSenderService;
import com.zipwhip.integration.zipchat.entities.OrgConfig;
import com.zipwhip.integration.zipchat.repository.OrgConfigRepository;
import com.zipwhip.integration.message.TextService;
import com.zipwhip.integration.message.TextServiceWrapper;
import com.zipwhip.integration.message.domain.MessageTracker;
import com.zipwhip.integration.test.junit.logging.LoggingExtension;
import com.zipwhip.message.service.proxy.ToolKitServiceFactory;
import com.zipwhip.message.utils.MessageUtils;
import com.zipwhip.security.LineOwnershipService;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@ExtendWith(LoggingExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SendTextServiceTest {

  @Mock
  private OrgConfigRepository mockOrgConfigRepository;

  @Mock
  private ToolKitServiceFactory mockToolkitServiceFactory;

  @Mock
  private LineOwnershipService mockLineValidator;

  @Mock
  private StatsSenderService mockStatsSenderService;

  @InjectMocks
  private SendTextServiceImpl service;

  @Mock
  private TextServiceWrapper mockTextService;

  private Long orgCustomerId;
  private TextService.Response sendTextResponse;

  private String successMsgResponse;
  private String badMobileMsgResponse;
  private String textBody;

  private String goodToken;
  private Long goodOrgCustomerId;
  private String claimId;
  private OrgConfig orgConfig;
  private TextService.Response sendResponse;

  @BeforeEach
  public void setup() {
    goodToken = "goodToken";
    goodOrgCustomerId = 5l;
    orgConfig = new OrgConfig();
    orgConfig.setOrgCustomerId(goodOrgCustomerId);
    claimId = "cla:4";

    textBody = "this is a text";

    doReturn(orgConfig).when(mockOrgConfigRepository).findByZipwhipToken(goodToken);
    sendResponse = new TextService.Response();
    sendResponse.setSuccess(true);
    sendResponse.setMessageId("2351352");
    doReturn(sendResponse).when(mockTextService)
      .send(any(String.class), any(String.class), any(String.class),
        any(MessageTracker.Origin.class));

    doReturn(true).when(mockLineValidator).doesOwnNumber(any(Long.class), any(String.class));

    service.setTextFromExternalEnabled(true);
    ReflectionTestUtils.setField(service, "integrationId", 20L);
  }

  @Test
  public void testFoundWrongOrgConfig() {
    //Setup
    String mobile = "5558675309";
    String landline = "5558675310";
    String body = "this is a text";
    long badOrgCustomerId = 1l;

    //Execute & Validate
    assertThrows(TokenMismatchException.class, () -> {
      service.validateAndRetrieveOrgConfig(goodToken, badOrgCustomerId, landline);
    });

    //Validate
    assertLogged(
      "ZipwhipToken " + goodToken + " does not match expected Org (expected orgCustomerId: "
        + badOrgCustomerId + ", but token belongs to " + goodOrgCustomerId + ")");
  }

  @Test
  public void testNoLandline() {
    //Setup
    String mobile = "5558675309";
    String landline = "5558675310";
    String body = "this is a text";
    long badOrgCustomerId = 1l;

    //Execute & Validate
    OrgConfig config = service.validateAndRetrieveOrgConfig(goodToken, badOrgCustomerId, "");

    //Validate
    assertNull(config);
  }

  @Test
  public void testNoOrgConfig() {
    //Setup
    String mobile = "5558675309";
    String landline = "5558675310";
    String body = "this is a text";
    long badOrgCustomerId = 1l;
    doReturn(null).when(mockOrgConfigRepository).findByZipwhipToken("badToken");

    //Execute & Validate
    assertThrows(TokenNotFoundException.class,
      () -> service.sendSms("badToken", badOrgCustomerId, mobile, landline, claimId, body));

    //Validate
    assertLogged("ZipwhipToken badToken found no orgConfig for orgCustomerId: " + badOrgCustomerId);
  }

  @Test
  public void testUnownedLandline() {
    //Setup
    String mobile = "5558675309";
    String landline = "5558675310";
    String body = "this is a text";
    doReturn(false).when(mockLineValidator).doesOwnNumber(any(Long.class), any(String.class));

    //Execute
    assertThrows(LandlineNotOwnedException.class,
      () -> service.sendSms(goodToken, goodOrgCustomerId, mobile, landline, claimId, body));

    //Validate
    assertLogged("OrgCustomer " + goodOrgCustomerId + " does not own landline: " + landline);

  }

  @Test
  public void testSentSuccessfully() {
    //Setup
    String mobile = "5558675309";
    String landline = "5558675310";
    String body = "this is a text";

    //Execute
    TextService.Response sent = service.sendSms(orgConfig, mobile, landline, claimId, body);

    //Validate
    assertTrue(sent.getSuccess());

  }

  @Test
  public void testSentFailure() {
    //Setup
    String mobile = "5558675309";
    String landline = "5558675310";
    String body = "this is a text";
    sendResponse = new TextService.Response();
    sendResponse.setSuccess(false);
    doReturn(sendResponse).when(mockTextService)
      .send(any(String.class), any(String.class), any(String.class),
        any(MessageTracker.Origin.class));

    //Execute
    TextService.Response sent = service
      .sendSms(goodToken, goodOrgCustomerId, mobile, landline, claimId, body);

    //Validate
    assertFalse(sent.getSuccess());
    assertLogged(
      "Unable to textback to " + mobile + " from " + MessageUtils.formatE164(landline) + ". Org ID "
        + goodOrgCustomerId);

  }

  @Test
  public void testBasicBatchCase() throws Exception {
    //Setup
    TextFromCrmResponse expected = new TextFromCrmResponse();
    orgConfig.setTraceFieldName("test");
    expected.setSuccess(true);
    expected.setResults(new HashMap<>());
    TextFromCrmRequest request = new TextFromCrmRequest();
    CrmMessage msg = addMessage(request, textBody, "+14258675309");
    request.setLandlineE164("4872036826");
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("test", "key");
    request.setHeaders(httpHeaders);

    //Execute
    TextFromCrmResponse response = service.sendMessages(goodToken, goodOrgCustomerId, request);

    //Validate
    expected.getResults().put(msg, successMsgResponse);
    assertNotLogged("Invalid mobile number provided");
    verify(mockTextService, times(1)).send(eq(MessageUtils.formatE164(request.getLandlineE164())),
      eq(MessageUtils.getRaw(msg.getMobileE164())), eq(msg.getBody()),
      any(MessageTracker.Origin.class));
    assertEquals(orgConfig.getTraceFieldName(), "test");
  }

  @Test
  public void testBasicBatchCaseNullTraceId() throws Exception {
    //Setup
    orgConfig.setTraceFieldName(null);
    TextFromCrmResponse expected = new TextFromCrmResponse();
    expected.setSuccess(true);
    expected.setResults(new HashMap<>());
    TextFromCrmRequest request = new TextFromCrmRequest();
    CrmMessage msg = addMessage(request, textBody, "+14258675309");
    request.setLandlineE164("4872036826");

    //Execute
    TextFromCrmResponse response = service.sendMessages(goodToken, goodOrgCustomerId, request);

    //Validate
    expected.getResults().put(msg, successMsgResponse);
    assertNotLogged("Invalid mobile number provided");
    verify(mockTextService, times(1)).send(eq(MessageUtils.formatE164(request.getLandlineE164())),
      eq(MessageUtils.getRaw(msg.getMobileE164())), eq(msg.getBody()),
      any(MessageTracker.Origin.class));
  }

  @Test
  public void testBasicBatchCaseNullAuthorize() {
    //Setup
    orgConfig.setTraceFieldName(null);
    TextFromCrmResponse expected = new TextFromCrmResponse();
    expected.setSuccess(true);
    expected.setResults(new HashMap<>());
    TextFromCrmRequest request = new TextFromCrmRequest();
    CrmMessage msg = addMessage(request, textBody, "+14258675309");
    request.setLandlineE164("4872036826");

    //Execute
    assertThrows(TokenNotFoundException.class,
      () -> service.sendMessages(null, goodOrgCustomerId, request));

    //Validate
    expected.getResults().put(msg, successMsgResponse);
  }

  @Test
  public void testLandlineNotOwned() throws Exception {
    //Setup
    String landline = "4872036826";
    doReturn(false).when(mockLineValidator).doesOwnNumber(any(Long.class), any(String.class));
    TextFromCrmRequest request = new TextFromCrmRequest();
    request.setLandlineE164(landline);

    //Execute & Validate
    assertThrows(LandlineNotOwnedException.class,
      () -> service.sendMessages(goodToken, goodOrgCustomerId, request));

    verify(mockTextService, never()).send(any(String.class), any(String.class), any(String.class),
      any(MessageTracker.Origin.class));

  }

  @Test
  public void testEmptyMessageList() throws Exception {
    //Setup
    TextFromCrmResponse expected = new TextFromCrmResponse();
    expected.setSuccess(true);
    expected.setResults(new HashMap<>());
    TextFromCrmRequest request = new TextFromCrmRequest();
    request.setLandlineE164("4872036826");

    //Execute & Validate
    assertThrows(IllegalArgumentException.class,
      () -> service.sendMessages(goodToken, goodOrgCustomerId, request));

    verify(mockTextService, never()).send(any(String.class), any(String.class), any(String.class),
      any(MessageTracker.Origin.class));

  }

  @Test
  public void testListOfNumbers() throws Exception {
    //Setup
    TextFromCrmRequest request = new TextFromCrmRequest();
    CrmMessage msgGood1 = addMessage(request, textBody, "4258675309");
    CrmMessage msgGood2 = addMessage(request, textBody, "2068675309");
    request.setLandlineE164("4872036826");

    //Execute
    TextFromCrmResponse response = service.sendMessages(goodToken, goodOrgCustomerId, request);

    //Validate
    assertNotLogged("Invalid mobile number provided");
    assertNotNull(response);
    assertTrue(response.isSuccess());
    assertTrue(response.getResults().get(msgGood1).contains("Success"));
    assertTrue(response.getResults().get(msgGood2).contains("Success"));
    verify(mockTextService, times(2)).send(any(String.class), any(String.class), any(String.class),
      any(MessageTracker.Origin.class));

  }

  @Test
  public void testListOfNumbersOneBad() throws Exception {
    //Setup
    TextFromCrmRequest request = new TextFromCrmRequest();
    CrmMessage msgGood1 = addMessage(request, textBody, "4258675309");
    CrmMessage msgBad1 = addMessage(request, textBody, "5558675309");
    CrmMessage msgGood2 = addMessage(request, textBody, "2068675309");
    request.setLandlineE164("4872036826");

    //Execute
    TextFromCrmResponse response = service.sendMessages(goodToken, goodOrgCustomerId, request);

    //Validate
    assertLogged("Invalid mobile number provided");
    assertNotNull(response);
    assertFalse(response.isSuccess());
    assertTrue(response.getResults().get(msgGood1).contains("Success"),
      "Doesn't contain 'Success': " + response.getResults().get(msgGood1));
    assertTrue(response.getResults().get(msgBad1).contains("Failed"),
      "Doesn't contain 'Failed': " + response.getResults().get(msgBad1));
    assertTrue(response.getResults().get(msgGood2).contains("Success"),
      "Doesn't contain 'Success': " + response.getResults().get(msgGood2));
    verify(mockTextService, times(2)).send(any(String.class), any(String.class), any(String.class),
      any(MessageTracker.Origin.class));

  }

  @Test
  public void testBadNumber() throws Exception {
    //Setup
    TextFromCrmRequest request = new TextFromCrmRequest();
    CrmMessage msg = addMessage(request, textBody, "5558675309");
    msg = addMessage(request, textBody, "4872036826");
    request.setLandlineE164("4872036826");

    //Execute
    TextFromCrmResponse response = service.sendMessages(goodToken, goodOrgCustomerId, request);

    assertLogged("Invalid mobile number provided");
    assertNotNull(response);
    assertFalse(response.isSuccess());
    verify(mockTextService, never()).send(any(String.class), any(String.class), any(String.class),
      any(MessageTracker.Origin.class));

  }

  @Test
  public void testNoMobile() throws Exception {
    //Setup
    TextFromCrmRequest request = new TextFromCrmRequest();
    CrmMessage msg = addMessage(request, textBody, "");
    request.setLandlineE164("4872036826");

    //Execute
    TextFromCrmResponse response = service.sendMessages(goodToken, goodOrgCustomerId, request);

    assertLogged("Message has no mobile to send to");
    assertNotNull(response);
    assertFalse(response.isSuccess());
    verify(mockTextService, never()).send(any(String.class), any(String.class), any(String.class),
      any(MessageTracker.Origin.class));

  }

  @Test
  public void testNoBody() throws Exception {
    //Setup
    TextFromCrmRequest request = new TextFromCrmRequest();
    CrmMessage msg = addMessage(request, null, "4258675309");
    request.setLandlineE164("4872036826");

    //Execute
    TextFromCrmResponse response = service.sendMessages(goodToken, goodOrgCustomerId, request);

    assertLogged("Message has no body to send");
    assertNotNull(response);
    assertFalse(response.isSuccess());
    verify(mockTextService, never()).send(any(String.class), any(String.class), any(String.class),
      any(MessageTracker.Origin.class));

  }

  @Test
  public void tesGeneralAuthError() throws Exception {
    //Setup
    doThrow(new RuntimeException("JUNIT_TEST_EXCEPTION")).when(mockOrgConfigRepository)
      .findByZipwhipToken(any(String.class));

    //Execute & Validate
    assertThrows(GeneralAuthenticationException.class,
      () -> service.validateAndRetrieveOrgConfig("badToken", 1l, "+15558675309"));

  }

  private CrmMessage addMessage(TextFromCrmRequest request, String body, String mobile) {
    CrmMessage msg = new CrmMessage();
    msg.setBody(body);
    msg.setMobileE164(mobile);
    if (request.getMessages() == null) {
      request.setMessages(new ArrayList<>());
    }
    request.getMessages().add(msg);
    return msg;
  }
}
