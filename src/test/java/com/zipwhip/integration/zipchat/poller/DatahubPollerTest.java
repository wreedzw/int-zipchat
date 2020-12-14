package com.zipwhip.integration.zipchat.poller;

import static com.zipwhip.integration.test.junit.logging.LoggingExtension.assertLogged;
import static com.zipwhip.integration.test.junit.logging.LoggingExtension.assertNotLogged;
import static com.zipwhip.service.MessageProcessingRecorder.ProcessingState.FAILED_TO_PROCESS;
import static com.zipwhip.service.MessageProcessingRecorder.ProcessingState.RECORD_PROCESSED;
import static com.zipwhip.service.MessageProcessingRecorder.RecordType.CONTACT;
import static com.zipwhip.service.MessageProcessingRecorder.RecordType.MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.google.common.util.concurrent.RateLimiter;
import com.zipwhip.integration.zipchat.entities.OrgConfig;
import com.zipwhip.integration.zipchat.repository.OrgConfigRepository;
import com.zipwhip.integration.zipchat.service.SyncService;
import com.zipwhip.integration.test.junit.logging.LoggingExtension;
import com.zipwhip.message.domain.InboundBase;
import com.zipwhip.message.domain.InboundContact;
import com.zipwhip.message.domain.InboundMessage;
import com.zipwhip.service.MessageProcessingRecorder;
import com.zipwhip.subscription.domain.SubscriptionInfo;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@ExtendWith(LoggingExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@Slf4j
@ActiveProfiles("kafka")
public class DatahubPollerTest {

  private static final int PARTITION = 1;

  private static final long PAYLOAD_ID = 1;

  private static final String APP_NAME = "ZipChat";

  private final String TOPIC = "junit_topic";

  @Mock
  private OrgConfigRepository mockOrgConfigRepository;

  @Mock
  private SyncService mockSyncService;

  @InjectMocks
  private DatahubPoller poller;

  private OrgConfig orgConfig;

  private RateLimiter rateLimit;

  @Mock
  private DatahubPoller.ZipChatExecutionContext mockContext;

  @Mock
  private SubscriptionInfo.SubscriptionRecord mockSubscriptionRecord;

  @Mock
  private MessageProcessingRecorder mockMessageProcessingRecorder;

  private ConsumerRecords<String, InboundBase<?>> records;
  private HashMap<TopicPartition, List<ConsumerRecord<String, InboundBase<?>>>> topicMap;

  private List<ConsumerRecord<String, InboundBase<?>>> recordList;

  @BeforeEach
  public void setup() {
    //Setup log validation

    //Setup log validation
    orgConfig = new OrgConfig();
    doReturn(Optional.of(orgConfig)).when(mockOrgConfigRepository).findById(any(Long.class));
    doReturn(true).when(mockMessageProcessingRecorder)
      .startProcessing(any(), anyLong(), anyString());

    //Setup context
    orgConfig.setOrgCustomerId(3l);
    doReturn(orgConfig).when(mockContext).getOrgConfig();
    doReturn(orgConfig.getOrgCustomerId()).when(mockSubscriptionRecord).getCustomerId();
    doReturn(mockSubscriptionRecord).when(mockContext).getSubscriptionRecord();

    //Setup records for consumption
    poller.setSyncEnabled(true);
    poller.setContactSyncEnabled(true);
    rateLimit = RateLimiter.create(1000);
    topicMap = new HashMap<>();
    recordList = new LinkedList<>();
    topicMap.put(new TopicPartition(TOPIC, PARTITION), recordList);
    records = new ConsumerRecords<>(topicMap);
    ReflectionTestUtils.setField(poller, "appName", APP_NAME);
  }

  @Test
  public void testRefreshSubscription() {
    //Setup
    doReturn(Optional.of(Mockito.mock(OrgConfig.class))).when(mockOrgConfigRepository)
      .findById(any(Long.class));

    //Execute
    poller.refreshSubscription(mockContext, mockSubscriptionRecord);

    //Validate
    verify(mockOrgConfigRepository, times(1)).findById(eq(orgConfig.getOrgCustomerId()));
    assertNotSame(orgConfig, mockContext.orgConfig);
  }

  @Test
  public void testCreateContext() {
    //Setup
    SubscriptionInfo.SubscriptionRecord record = new SubscriptionInfo.SubscriptionRecord();
    record.setCustomerId(3l);

    //Execute
    DatahubPoller.ZipChatExecutionContext context = poller.createContext(record);

    //Validate
    assertEquals(orgConfig, context.orgConfig);
    assertTrue(orgConfig.equals(context.orgConfig));
    assertTrue(orgConfig.hashCode() == context.orgConfig.hashCode());
  }

  @Test
  public void testCreateEqualContexts() {
    //Setup
    SubscriptionInfo.SubscriptionRecord record = new SubscriptionInfo.SubscriptionRecord();
    record.setCustomerId(3l);

    //Execute
    DatahubPoller.ZipChatExecutionContext context1 = poller.createContext(record);
    DatahubPoller.ZipChatExecutionContext context2 = poller.createContext(record);

    //Validate
    assertTrue(context1.canEqual(context2));
    assertTrue(context1.equals(context2));
    assertEquals(context1.hashCode(), context2.hashCode());
    assertEquals(context1.toString(), context2.toString());
  }

  @Test
  public void testConsume() {
    //Setup
    poller.setSyncEnabled(true);

    //Execute
    poller.consume(records, mockContext, rateLimit);

    //Validate
    verify(mockContext, times(1)).commit();

  }

  @Test
  public void testConsumeSyncDisabled() {
    //Setup
    poller.setSyncEnabled(false);

    //Execute
    poller.consume(records, mockContext, rateLimit);

    //Validate
    verify(mockContext, times(0)).commit();
    assertLogged("Sync feature disabled at service level, skipping");

  }

  @Test
  public void testConsumeException() {
    //Setup
    recordList.add(buildMessageRecord());
    doThrow(new RuntimeException("JUNIT EXCEPTION TEST")).when(mockContext).getOrgConfig();

    //Execute
    assertThrows(RuntimeException.class, () -> {
      poller.consume(records, mockContext, rateLimit);
    });

    //Validate
    verify(mockContext, times(0)).commit();

    assertLogged("Unhandled error");

  }

  @Test
  public void testProcessRecord() {
    //Setup
    recordList.add(buildMessageRecord());

    //Execute
    poller.consume(records, mockContext, rateLimit);

    //Validate
    verify(mockContext, times(1)).commit();
    verify(mockSyncService, times(1))
      .processMessage(any(InboundMessage.class), any(OrgConfig.class));
    verify(mockMessageProcessingRecorder, times(1)).startProcessing(MESSAGE, PAYLOAD_ID, APP_NAME);
    verify(mockMessageProcessingRecorder, times(1))
      .completeProcessing(RECORD_PROCESSED, MESSAGE, PAYLOAD_ID, APP_NAME);

    assertNotLogged("Sync feature disabled at service level, skipping");
    assertNotLogged("Failed to process message");
  }

  @Test
  public void testProcessContactRecord() {
    //Setup
    recordList.add(buildContactRecord());

    //Execute
    poller.consume(records, mockContext, rateLimit);

    //Validate
    verify(mockContext, times(1)).commit();
    verify(mockSyncService, times(1))
      .processContact(any(InboundContact.class), any(OrgConfig.class));
    verify(mockMessageProcessingRecorder, times(1)).startProcessing(CONTACT, PAYLOAD_ID, APP_NAME);
    verify(mockMessageProcessingRecorder, times(1))
      .completeProcessing(RECORD_PROCESSED, CONTACT, PAYLOAD_ID, APP_NAME);
    assertNotLogged("Contact Sync feature disabled at service level, skipping");
    assertNotLogged("Failed to process contact");
  }

  @Test
  public void testForceProcessDisabled() {
    //Setup
    poller.setSyncEnabled(false);

    //Execute
    poller.forceProcessRecord(buildMessage(), orgConfig.getOrgCustomerId());

    //Validate
    verify(mockContext, times(0)).commit();
    assertLogged("Sync feature disabled at service level, skipping");

  }

  @Test
  public void testForceProcessNullOrgConfig() {
    //Setup
    poller.setSyncEnabled(false);

    doReturn(Optional.ofNullable(null)).when(mockOrgConfigRepository).findById(any(Long.class));

    //Execute
    poller.forceProcessRecord(buildMessage(), orgConfig.getOrgCustomerId());

    //Validate
    verify(mockContext, times(0)).commit();
    assertLogged("No org config found for 3; cannot process message");

  }

  @Test
  public void testForceProcessContactDisabled() {
    //Setup
    poller.setSyncEnabled(true);
    poller.setContactSyncEnabled(false);

    //Execute
    poller.forceProcessRecord(buildContact(), orgConfig.getOrgCustomerId());

    //Validate
    verify(mockContext, times(0)).commit();
    assertLogged("Contact Sync feature disabled at service level, skipping");

  }

  @Test
  public void testConsumeMessageFailed() {
    //Setup
    doThrow(new RuntimeException("JUNIT_EXCEPTION")).when(mockSyncService)
      .processMessage(any(InboundMessage.class), any(OrgConfig.class));
    recordList.add(buildMessageRecord());

    //Execute
    poller.consume(records, mockContext, rateLimit);

    //Validate
    verify(mockContext, times(1)).commit();
    verify(mockSyncService, times(1))
      .processMessage(any(InboundMessage.class), any(OrgConfig.class));
    verify(mockMessageProcessingRecorder, times(1)).startProcessing(MESSAGE, PAYLOAD_ID, APP_NAME);
    verify(mockMessageProcessingRecorder, times(1))
      .completeProcessing(FAILED_TO_PROCESS, MESSAGE, PAYLOAD_ID, APP_NAME);

    assertNotLogged("Sync feature disabled at service level, skipping");
    assertLogged("Failed to process message");
  }

  @Test
  public void testConsumeContactFailed() {
    //Setup
    doThrow(new RuntimeException("JUNIT_EXCEPTION")).when(mockSyncService)
      .processContact(any(InboundContact.class), any(OrgConfig.class));
    recordList.add(buildContactRecord());

    //Execute
    poller.consume(records, mockContext, rateLimit);

    //Validate
    verify(mockContext, times(1)).commit();
    verify(mockSyncService, times(1))
      .processContact(any(InboundContact.class), any(OrgConfig.class));
    verify(mockMessageProcessingRecorder, times(1)).startProcessing(CONTACT, PAYLOAD_ID, APP_NAME);
    verify(mockMessageProcessingRecorder, times(1))
      .completeProcessing(FAILED_TO_PROCESS, CONTACT, PAYLOAD_ID, APP_NAME);

    assertNotLogged("Sync feature disabled at service level, skipping");
    assertLogged("Failed to process contact");
  }

  private ConsumerRecord<String, InboundBase<?>> buildMessageRecord() {
    return new ConsumerRecord<String, InboundBase<?>>("forced", 0, 0, null, buildMessage());
  }

  private ConsumerRecord<String, InboundBase<?>> buildContactRecord() {
    return new ConsumerRecord<String, InboundBase<?>>("forced", 0, 0, null, buildContact());
  }

  private InboundBase<?> buildMessage() {
    InboundMessage m = new InboundMessage();
    m.setPayload(new InboundMessage.Payload());
    m.getPayload().setBody("Oh hai Mark");
    m.getPayload().setSourceAddress("5558675309");
    m.getPayload().setDestAddress("5555555555");
    m.getPayload().setId(PAYLOAD_ID);
    return m;
  }

  private InboundBase<?> buildContact() {
    InboundContact c = new InboundContact();
    c.setPayload(new InboundContact.Payload());
    c.getPayload().setLastName("Jones");
    c.getPayload().setFirstName("Jessica");
    c.getPayload().setPhoneNumber("5558675309");
    c.getPayload().setId(PAYLOAD_ID);
    c.setAction("new");
    return c;
  }
}
