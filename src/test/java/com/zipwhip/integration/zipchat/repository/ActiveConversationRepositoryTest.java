package com.zipwhip.integration.zipchat.repository;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ActiveConversationRepositoryTest {

  @Mock
  private SimpleActiveConversationRepository mockSimpleActiveConversationRepository;

  @InjectMocks
  private ActiveConversationRepository activeConversationRepository;

  @Test
  public void testSave() {
    //Setup
    ConversationId id = buildConversationId(5l);
    ActiveConversation convo = new ActiveConversation(id);

    //Execute
    activeConversationRepository.save(convo);

    //Validate
    verify(mockSimpleActiveConversationRepository, times(1)).save(eq(convo));

  }

  @Test
  public void testDelete() {
    //Setup
    ConversationId id = buildConversationId(5l);
    ActiveConversation convo = new ActiveConversation(id);

    //Execute
    activeConversationRepository.delete(convo);

    //Validate
    verify(mockSimpleActiveConversationRepository, times(1)).delete(eq(convo));

  }

  @Test
  public void testFindByConversationId() {
    //Setup
    ConversationId id = buildConversationId(5l);

    //Execute
    activeConversationRepository.findById(id);

    //Validate
    verify(mockSimpleActiveConversationRepository, times(1)).findById(eq(id));

  }

  @Test
  public void testGetByOrgCustomerId() {
    //Setup

    //Execute
    activeConversationRepository.getByOrgCustomerId(5l);

    //Validate
    verify(mockSimpleActiveConversationRepository, times(1)).getByOrgCustomerId(eq(5l));

  }

  @Test
  public void testFindAll() {
    //Setup

    //Execute
    activeConversationRepository.findAll();

    //Validate
    verify(mockSimpleActiveConversationRepository, times(1)).findAll();

  }

  public ConversationId buildConversationId(Long orgCustomerId) {
    ConversationId id = new ConversationId(orgCustomerId, "5558675309", "2228675309",
      "claim:JUNIT");
    return id;
  }
}
