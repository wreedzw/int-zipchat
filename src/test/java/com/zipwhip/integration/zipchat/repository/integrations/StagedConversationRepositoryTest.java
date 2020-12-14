package com.zipwhip.integration.zipchat.repository.integrations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.zipwhip.integration.zipchat.domain.SyncableMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

@ActiveProfiles("mongo")
@DataMongoTest
@ContextConfiguration(classes = TestDataConfiguration.class)
public class StagedConversationRepositoryTest {

  private final static String START_MESSAGE_TIME = "2019-01-01 01:00:01";

  private final static String END_MESSAGE_TIME = "2019-02-01 23:59:59";

  @Autowired
  private StagedNoteRepository repository;

  @BeforeEach
  public void init() {
    final SimpleStagedNoteRepository wrappedRepo = (SimpleStagedNoteRepository) ReflectionTestUtils
      .getField(repository, "wrappedRepository");
    wrappedRepo.deleteAll();
    wrappedRepo.saveAll(buildStagedConversations());
    assertEquals(2, wrappedRepo.count());
  }

  @Test
  public void testFindByOrgCustomerIdNoParams() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndClaimPublicId() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("claimPublicId", RepositoryTestUtils.CLAIM_PUBLIC_ID);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndDocumentPublicId() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("documentPublicId", RepositoryTestUtils.DOCUMENT_PUBLIC_ID);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndDocumentId() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("documentId", String.valueOf(RepositoryTestUtils.DOCUMENT_ID));
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndFirstMessageStart() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfFirstMessageStart", START_MESSAGE_TIME);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdClaimPublicIdAndFirstMessageStart() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("claimPublicId", "claim:2");
    params.put("windowOfFirstMessageStart", START_MESSAGE_TIME);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdDocumentPublicIdAndFirstMessageStart() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("documentPublicId", RepositoryTestUtils.DOCUMENT_PUBLIC_ID);
    params.put("windowOfFirstMessageStart", START_MESSAGE_TIME);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdDocumentIdAndFirstMessageStart() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("documentId", String.valueOf(RepositoryTestUtils.DOCUMENT_ID));
    params.put("windowOfFirstMessageStart", START_MESSAGE_TIME);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndLastMessageStart() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfLastMessageStart", START_MESSAGE_TIME);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndLastMessageEnd() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfLastMessageEnd", END_MESSAGE_TIME);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdClaimPublicIdAndLastMessageStart() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("claimPublicId", "claim:2");
    params.put("windowOfLastMessageStart", START_MESSAGE_TIME);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdDocumentPublicIdAndLastMessageStart() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("documentPublicId", RepositoryTestUtils.DOCUMENT_PUBLIC_ID);
    params.put("windowOfLastMessageStart", START_MESSAGE_TIME);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdDocumentIdAndLastMessageStart() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("documentId", String.valueOf(RepositoryTestUtils.DOCUMENT_ID));
    params.put("windowOfLastMessageStart", START_MESSAGE_TIME);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndFirstMessageEnd() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfFirstMessageEnd", START_MESSAGE_TIME);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdClaimPublicIdAndFirstMessageEnd() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("claimPublicId", RepositoryTestUtils.CLAIM_PUBLIC_ID);
    params.put("windowOfFirstMessageEnd", START_MESSAGE_TIME);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdDocumentPublicIdAndFirstMessageEnd() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("documentPublicId", RepositoryTestUtils.DOCUMENT_PUBLIC_ID);
    params.put("windowOfFirstMessageEnd", START_MESSAGE_TIME);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdDocumentIdAndFirstMessageEnd() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("documentId", String.valueOf(RepositoryTestUtils.DOCUMENT_ID));
    params.put("windowOfFirstMessageEnd", END_MESSAGE_TIME);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndTimeWindowOfFirstMessage() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfFirstMessageStart", START_MESSAGE_TIME);
    params.put("windowOfFirstMessageEnd", END_MESSAGE_TIME);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndTimeWindowOfFirstMessageUnknownDatesFormat() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfFirstMessageStart", RepositoryTestUtils.UNKNOWN_DATE_FORMAT);
    params.put("windowOfFirstMessageEnd", RepositoryTestUtils.UNKNOWN_DATE_FORMAT);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testFindByOrgCustomerIdAndTimeWindowOfFirstMessageUnknownStartDateFormat() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfFirstMessageStart", RepositoryTestUtils.UNKNOWN_DATE_FORMAT);
    params.put("windowOfFirstMessageEnd", END_MESSAGE_TIME);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testFindByOrgCustomerIdAndTimeWindowOfFirstMessageUnknownEndDateFormat() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfFirstMessageStart", START_MESSAGE_TIME);
    params.put("windowOfFirstMessageEnd", RepositoryTestUtils.UNKNOWN_DATE_FORMAT);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testFindByOrgCustomerIdAndTimeWindowOfLastMessage() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfLastMessageStart", START_MESSAGE_TIME);
    params.put("windowOfLastMessageEnd", END_MESSAGE_TIME);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndTimeWindowOfLastMessageUnknownDatesFormat() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfLastMessageStart", RepositoryTestUtils.UNKNOWN_DATE_FORMAT);
    params.put("windowOfLastMessageEnd", RepositoryTestUtils.UNKNOWN_DATE_FORMAT);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testFindByOrgCustomerIdAndTimeWindowOfLastMessageUnknownStartDateFormat() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfLastMessageStart", RepositoryTestUtils.UNKNOWN_DATE_FORMAT);
    params.put("windowOfLastMessageEnd", END_MESSAGE_TIME);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testFindByOrgCustomerIdAndTimeWindowOfLastMessageUnknownEndDateFormat() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfLastMessageStart", START_MESSAGE_TIME);
    params.put("windowOfLastMessageEnd", RepositoryTestUtils.UNKNOWN_DATE_FORMAT);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testFindByOrgCustomerIdAndConvWindowStart() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfConversationWindowStart", START_MESSAGE_TIME);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndConvWindowStartUnknownFormat() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfConversationWindowStart", RepositoryTestUtils.UNKNOWN_DATE_FORMAT);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testFindByOrgCustomerIdAndConvWindowEnd() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfConversationWindowEnd", END_MESSAGE_TIME);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndConvWindowEndUnknownFormat() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfConversationWindowEnd", RepositoryTestUtils.UNKNOWN_DATE_FORMAT);
    final List<StagedNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  private List<StagedNote> buildStagedConversations() {
    final List<StagedNote> conversations = new ArrayList<>();

    //Create first staged note
    final ConversationId id = new ConversationId(RepositoryTestUtils.ORG_CUSTOMER_ID, "5558675309", "2228675309",
      RepositoryTestUtils.CLAIM_PUBLIC_ID);
    final ActiveConversation activeConversation = new ActiveConversation(id);
    activeConversation.setWindow(RepositoryTestUtils.buildConversationWindow());
    final ClosedConversation closedConversation = new ClosedConversation(activeConversation);
    final SyncableMessage syncableMessage = new SyncableMessage();
    syncableMessage.setSubject("test");
    syncableMessage.setBody("Body test");
    syncableMessage.setLandlineE164("+123456789");
    syncableMessage.setMobileE164("+123456789");
    final StagedNote stagedNote = new StagedNote(closedConversation,
      RepositoryTestUtils.buildConversationMessages(), syncableMessage);

    //Create second closed conversation
    final ConversationId id2 = new ConversationId(RepositoryTestUtils.ORG_CUSTOMER_ID, "5558675310", "2228675310",
      "claim:2");
    final ActiveConversation activeConversation2 = new ActiveConversation(id2);
    activeConversation2.setMessages(RepositoryTestUtils.buildConversationMessages());
    final ConversationWindow window = RepositoryTestUtils.buildConversationWindow();
    window.setFirstMessage(RepositoryTestUtils.MESSAGE_TIME);
    window.setLastMessage(RepositoryTestUtils.MESSAGE_TIME);
    activeConversation2.setWindow(window);
    final ClosedConversation closedConversation2 = new ClosedConversation(activeConversation2);
    closedConversation2.setClosedDate(new DateTime(RepositoryTestUtils.MESSAGE_TIME));
    final StagedNote stagedNote2 = new StagedNote(closedConversation2,
      RepositoryTestUtils.buildConversationMessages(), null);

    conversations.add(stagedNote);
    conversations.add(stagedNote2);

    return conversations;
  }

}

