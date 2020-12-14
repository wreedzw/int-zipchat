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
public class ClosedConversationRepositoryTest {

  @Mock
  private SimpleClosedConversationRepository mockSimpleClosedConversationRepository;

  @InjectMocks
  private ClosedConversationRepository stagedConversationRepository;

  @Test
  public void testDelete() {
    //Setup
    ConversationId id = buildConversationId(5l);
    ActiveConversation activeConvo = new ActiveConversation(id);
    ClosedConversation convo = new ClosedConversation(activeConvo);

    //Execute
    stagedConversationRepository.delete(convo);

    //Validate
    verify(mockSimpleClosedConversationRepository, times(1)).delete(eq(convo));

  }

  @Test
  public void testFindByConversationId() {
    //Setup
    ConversationId id = buildConversationId(5l);

    //Execute
    stagedConversationRepository.findByConversationId(id);

    //Validate
    verify(mockSimpleClosedConversationRepository, times(1)).findByConversationId(eq(id));

  }

  @Test
  public void testSave() {
    //Setup
    ConversationId id = buildConversationId(5l);
    ActiveConversation activeConvo = new ActiveConversation(id);
    ClosedConversation convo = new ClosedConversation(activeConvo);

    //Execute
    stagedConversationRepository.save(convo);

    //Validate
    verify(mockSimpleClosedConversationRepository, times(1)).save(eq(convo));

  }

  @Test
  public void testGetByOrgCustomerId() {
    //Setup

    //Execute
    stagedConversationRepository.getToStageByOrgCustomerId(5l);

    //Validate
    verify(mockSimpleClosedConversationRepository, times(1)).getToStageByOrgCustomerId(eq(5l));

  }

  public ConversationId buildConversationId(Long orgCustomerId) {
    ConversationId id = new ConversationId(orgCustomerId, "5558675309", "2228675309",
      "claim:JUNIT");
    return id;
  }
}
