package com.zipwhip.integration.zipchat.repository.integrations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.zipwhip.integration.zipchat.domain.SyncableMessage;
import com.zipwhip.integration.zipchat.entities.StoredNote;

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
public class StoredConversationRepositoryTest {

  private final static String START_MESSAGE_TIME = "2019-01-01 01:00:01";

  private final static String END_MESSAGE_TIME = "2019-02-01 23:59:59";

  @Autowired
  private StoredNoteRepository repository;

  @BeforeEach
  public void init() {
    final SimpleStoredNoteRepository wrappedRepo = (SimpleStoredNoteRepository) ReflectionTestUtils
      .getField(repository, "wrappedRepository");
    wrappedRepo.deleteAll();
    wrappedRepo.saveAll(buildStoredConversations());
    assertEquals(2, wrappedRepo.count());
  }

  @Test
  public void testFindByOrgCustomerIdNoParams() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    final List<StoredNote> result = repository
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
    final List<StoredNote> result = repository
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
    final List<StoredNote> result = repository
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
    final List<StoredNote> result = repository
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
    final List<StoredNote> result = repository
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
    final List<StoredNote> result = repository
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
    final List<StoredNote> result = repository
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
    final List<StoredNote> result = repository
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
    final List<StoredNote> result = repository
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
    final List<StoredNote> result = repository
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
    final List<StoredNote> result = repository
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
    final List<StoredNote> result = repository
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
    final List<StoredNote> result = repository
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
    final List<StoredNote> result = repository
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
    final List<StoredNote> result = repository
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
    final List<StoredNote> result = repository
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
    params.put("windowOfFirstMessageEnd", START_MESSAGE_TIME);
    final List<StoredNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndTimeWindowOfFirstMessage() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfFirstMessageStart", START_MESSAGE_TIME);
    params.put("windowOfFirstMessageEnd", END_MESSAGE_TIME);
    final List<StoredNote> result = repository
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
    final List<StoredNote> result = repository
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
    final List<StoredNote> result = repository
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
    final List<StoredNote> result = repository
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
    final List<StoredNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndTimeWindowOfLastMessageUnknownDates() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfLastMessageStart", RepositoryTestUtils.UNKNOWN_DATE_FORMAT);
    params.put("windowOfLastMessageEnd", RepositoryTestUtils.UNKNOWN_DATE_FORMAT);
    final List<StoredNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testFindByOrgCustomerIdAndTimeWindowOfLastMessageUnknownStartDate() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfLastMessageStart", RepositoryTestUtils.UNKNOWN_DATE_FORMAT);
    params.put("windowOfLastMessageEnd", END_MESSAGE_TIME);
    final List<StoredNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testFindByOrgCustomerIdAndTimeWindowOfLastMessageUnknownEndDate() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfLastMessageStart", START_MESSAGE_TIME);
    params.put("windowOfLastMessageEnd", RepositoryTestUtils.UNKNOWN_DATE_FORMAT);
    final List<StoredNote> result = repository
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
    final List<StoredNote> result = repository
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
    final List<StoredNote> result = repository
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
    final List<StoredNote> result = repository
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
    final List<StoredNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testFindByOrgCustomerIdAndNotePublicId() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("notePublicId", RepositoryTestUtils.NOTE_PUBLIC_ID);
    final List<StoredNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndTimeWindowNoteStorage() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfNoteStorageStart", START_MESSAGE_TIME);
    params.put("windowOfNoteStorageEnd", END_MESSAGE_TIME);
    final List<StoredNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndTimeWindowNoteStorageUnknownDatesFormat() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfNoteStorageStart", RepositoryTestUtils.UNKNOWN_DATE_FORMAT);
    params.put("windowOfNoteStorageEnd", RepositoryTestUtils.UNKNOWN_DATE_FORMAT);
    final List<StoredNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testFindByOrgCustomerIdAndTimeWindowNoteStorageUnknownStartDateFormat() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfNoteStorageStart", RepositoryTestUtils.UNKNOWN_DATE_FORMAT);
    params.put("windowOfNoteStorageEnd", END_MESSAGE_TIME);
    final List<StoredNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testFindByOrgCustomerIdAndTimeWindowNoteStorageUnknownEndDateFormat() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfNoteStorageStart", START_MESSAGE_TIME);
    params.put("windowOfNoteStorageEnd", RepositoryTestUtils.UNKNOWN_DATE_FORMAT);
    final List<StoredNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndStorageDateStart() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfNoteStorageStart", START_MESSAGE_TIME);
    final List<StoredNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndStorageDateStartUnknownFormat() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfNoteStorageStart", RepositoryTestUtils.UNKNOWN_DATE_FORMAT);
    final List<StoredNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testFindByOrgCustomerIdAndStorageDateEnd() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfNoteStorageEnd", END_MESSAGE_TIME);
    final List<StoredNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndStorageDateEndUnknownFormat() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfNoteStorageEnd", RepositoryTestUtils.UNKNOWN_DATE_FORMAT);
    final List<StoredNote> result = repository
      .findByOrgCustomerIdAndAlsoParams(RepositoryTestUtils.ORG_CUSTOMER_ID, params);

    //Validate
    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(repository.getByOrgCustomerId(RepositoryTestUtils.ORG_CUSTOMER_ID).size(), result.size());
  }

  private List<StoredNote> buildStoredConversations() {
    final List<StoredNote> conversations = new ArrayList<>();
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
    final StoredNote storedNote = new StoredNote(stagedNote, RepositoryTestUtils.NOTE_PUBLIC_ID);
    storedNote.setStoredDate(new DateTime(RepositoryTestUtils.FIRST_MESSAGE_TIME));

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
    final StoredNote storedNote2 = new StoredNote(stagedNote2, "note:2");
    storedNote2.setStoredDate(new DateTime(RepositoryTestUtils.MESSAGE_TIME));

    conversations.add(storedNote);
    conversations.add(storedNote2);

    return conversations;
  }

}
