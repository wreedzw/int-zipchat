package com.zipwhip.integration.zipchat.repository;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.zipwhip.integration.zipchat.domain.SyncableMessage;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StagedConversationRepositoryTest {

  @Mock
  private SimpleStagedNoteRepository mockSimpleStagedNoteRepository;

  @InjectMocks
  private StagedNoteRepository stagedNoteRepository;

  @Test
  public void testSave() {
    //Setup
    ConversationId id = buildConversationId(5l);
    ActiveConversation activeConvo = new ActiveConversation(id);
    ClosedConversation closedConvo = new ClosedConversation(activeConvo);
    StagedNote note = new StagedNote(closedConvo, new ArrayList<>(), new SyncableMessage());

    //Execute
    stagedNoteRepository.save(note);

    //Validate
    verify(mockSimpleStagedNoteRepository, times(1)).save(eq(note));

  }

  @Test
  public void testDelete() {
    //Setup
    ConversationId id = buildConversationId(5l);
    ActiveConversation activeConvo = new ActiveConversation(id);
    ClosedConversation closedConvo = new ClosedConversation(activeConvo);
    StagedNote note = new StagedNote(closedConvo, new ArrayList<>(), new SyncableMessage());

    //Execute
    stagedNoteRepository.delete(note);

    //Validate
    verify(mockSimpleStagedNoteRepository, times(1)).delete(eq(note));

  }

  @Test
  public void testGetByOrgCustomerId() {
    //Setup

    //Execute
    stagedNoteRepository.getByOrgCustomerId(5l);

    //Validate
    verify(mockSimpleStagedNoteRepository, times(1)).getByOrgCustomerId(eq(5l));

  }

  @Test
  public void testFindByConversationId() {
    //Setup
    ConversationId id = buildConversationId(5l);

    //Execute
    stagedNoteRepository.findByConversationId(id);

    //Validate
    verify(mockSimpleStagedNoteRepository, times(1)).findByConversationId(eq(id));

  }

  public ConversationId buildConversationId(Long orgCustomerId) {
    ConversationId id = new ConversationId(orgCustomerId, "5558675309", "2228675309",
      "claim:JUNIT");
    return id;
  }
}
