package com.zipwhip.integration.zipchat.repository.integrations;

import static com.zipwhip.integration.zipchat.repository.integrations.RepositoryTestUtils.CLAIM_PUBLIC_ID;
import static com.zipwhip.integration.zipchat.repository.integrations.RepositoryTestUtils.DOCUMENT_ID;
import static com.zipwhip.integration.zipchat.repository.integrations.RepositoryTestUtils.DOCUMENT_PUBLIC_ID;
import static com.zipwhip.integration.zipchat.repository.integrations.RepositoryTestUtils.MESSAGE_TIME;
import static com.zipwhip.integration.zipchat.repository.integrations.RepositoryTestUtils.ORG_CUSTOMER_ID;
import static com.zipwhip.integration.zipchat.repository.integrations.RepositoryTestUtils.UNKNOWN_DATE_FORMAT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class ActiveConversationRepositoryTest {

  private final static String START_MESSAGE_TIME = "2019-01-01 01:00:01";

  private final static String END_MESSAGE_TIME = "2019-02-01 23:59:59";

  @Autowired
  private ActiveConversationRepository repository;

  @BeforeEach
  public void init() {
    final SimpleActiveConversationRepository wrappedRepo = (SimpleActiveConversationRepository) ReflectionTestUtils
      .getField(repository, "wrappedRepository");
    wrappedRepo.deleteAll();
    wrappedRepo.saveAll(buildActiveConversations());
    assertEquals(2, wrappedRepo.count());
  }

  @Test
  public void testFindByOrgCustomerIdNoParams() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndClaimPublicId() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("claimPublicId", CLAIM_PUBLIC_ID);
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndDocumentPublicId() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("documentPublicId", DOCUMENT_PUBLIC_ID);
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndDocumentId() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("documentId", String.valueOf(DOCUMENT_ID));
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndFirstMessageStart() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfFirstMessageStart", START_MESSAGE_TIME);
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

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
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdDocumentPublicIdAndFirstMessageStart() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("documentPublicId", DOCUMENT_PUBLIC_ID);
    params.put("windowOfFirstMessageStart", START_MESSAGE_TIME);
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdDocumentIdAndFirstMessageStart() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("documentId", String.valueOf(DOCUMENT_ID));
    params.put("windowOfFirstMessageStart", START_MESSAGE_TIME);
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndLastMessageStart() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfLastMessageStart", START_MESSAGE_TIME);
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndLastMessageEnd() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfLastMessageEnd", END_MESSAGE_TIME);
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

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
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdDocumentPublicIdAndLastMessageStart() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("documentPublicId", DOCUMENT_PUBLIC_ID);
    params.put("windowOfLastMessageStart", START_MESSAGE_TIME);
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdDocumentIdAndLastMessageStart() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("documentId", String.valueOf(DOCUMENT_ID));
    params.put("windowOfLastMessageStart", START_MESSAGE_TIME);
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndFirstMessageEnd() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfFirstMessageEnd", END_MESSAGE_TIME);
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdClaimPublicIdAndFirstMessageEnd() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("claimPublicId", CLAIM_PUBLIC_ID);
    params.put("windowOfFirstMessageEnd", END_MESSAGE_TIME);
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdDocumentPublicIdAndFirstMessageEnd() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("documentPublicId", DOCUMENT_PUBLIC_ID);
    params.put("windowOfFirstMessageEnd", END_MESSAGE_TIME);
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdDocumentIdAndFirstMessageEnd() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("documentId", String.valueOf(DOCUMENT_ID));
    params.put("windowOfFirstMessageEnd", END_MESSAGE_TIME);
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

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
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndTimeWindowOfFirstMessageUnknownDatesFormat() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfFirstMessageStart", UNKNOWN_DATE_FORMAT);
    params.put("windowOfFirstMessageEnd", UNKNOWN_DATE_FORMAT);
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

    //Validate
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testFindByOrgCustomerIdAndTimeWindowOfFirstMessageUnknownStartDateFormat() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfFirstMessageStart", UNKNOWN_DATE_FORMAT);
    params.put("windowOfFirstMessageEnd", END_MESSAGE_TIME);
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

    //Validate
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testFindByOrgCustomerIdAndTimeWindowOfFirstMessageUnknownEndDateFormat() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfFirstMessageStart", START_MESSAGE_TIME);
    params.put("windowOfFirstMessageEnd", UNKNOWN_DATE_FORMAT);
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

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
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndTimeWindowOfLastMessageUnknownDatesFormat() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfLastMessageStart", UNKNOWN_DATE_FORMAT);
    params.put("windowOfLastMessageEnd", UNKNOWN_DATE_FORMAT);
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

    //Validate
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testFindByOrgCustomerIdAndTimeWindowOfLastMessageUnknownStartDateFormat() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfLastMessageStart", UNKNOWN_DATE_FORMAT);
    params.put("windowOfLastMessageEnd", END_MESSAGE_TIME);
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

    //Validate
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testFindByOrgCustomerIdAndTimeWindowOfLastMessageUnknownEndDateFormat() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfLastMessageStart", START_MESSAGE_TIME);
    params.put("windowOfLastMessageEnd", UNKNOWN_DATE_FORMAT);
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

    //Validate
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testFindByOrgCustomerIdAndConvWindowStart() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfConversationWindowStart", START_MESSAGE_TIME);
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndConvWindowStartUnknownFormat() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfConversationWindowStart", UNKNOWN_DATE_FORMAT);
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

    //Validate
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testFindByOrgCustomerIdAndConvWindowEnd() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfConversationWindowEnd", END_MESSAGE_TIME);
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

    //Validate
    assertFalse(result.isEmpty());
    assertEquals(2, result.size());
  }

  @Test
  public void testFindByOrgCustomerIdAndConvWindowEndUnknownFormat() {
    //Execute
    final Map<String, String> params = new HashMap<>();
    params.put("windowOfConversationWindowEnd", UNKNOWN_DATE_FORMAT);
    final List<ActiveConversation> result = repository
      .findByOrgCustomerIdAndAlsoParams(ORG_CUSTOMER_ID, params);

    //Validate
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  private List<ActiveConversation> buildActiveConversations() {
    final List<ActiveConversation> conversations = new ArrayList<>();

    //Create first active conversation
    final ConversationId id = new ConversationId(ORG_CUSTOMER_ID, "5558675309", "2228675309",
      CLAIM_PUBLIC_ID);
    final ActiveConversation conversation = new ActiveConversation(id);
    conversation.setMessages(RepositoryTestUtils.buildConversationMessages());
    conversation.setWindow(RepositoryTestUtils.buildConversationWindow());

    //Create second active conversation
    final ConversationId id2 = new ConversationId(5L, "5558675310", "2228675310", "claim:2");
    final ActiveConversation conversation2 = new ActiveConversation(id2);
    conversation2.setMessages(RepositoryTestUtils.buildConversationMessages());
    final ConversationWindow window = RepositoryTestUtils.buildConversationWindow();
    window.setFirstMessage(MESSAGE_TIME);
    window.setLastMessage(MESSAGE_TIME);
    conversation2.setWindow(window);

    conversations.add(conversation);
    conversations.add(conversation2);

    return conversations;
  }

}
