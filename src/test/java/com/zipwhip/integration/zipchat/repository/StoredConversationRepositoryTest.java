package com.zipwhip.integration.zipchat.repository;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.zipwhip.integration.zipchat.domain.SyncableMessage;
import com.zipwhip.integration.zipchat.entities.StoredNote;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StoredConversationRepositoryTest {

  @Mock
  private SimpleStoredNoteRepository mockSimpleStoredNoteRepository;

  @InjectMocks
  private StoredNoteRepository storedNoteRepository;

  @Test
  public void testSave() {
    //Setup
    ConversationId id = buildConversationId(5l);
    ActiveConversation activeConvo = new ActiveConversation(id);
    ClosedConversation closedConvo = new ClosedConversation(activeConvo);
    StagedNote staged = new StagedNote(closedConvo, new ArrayList<>(), new SyncableMessage());
    StoredNote note = new StoredNote(staged, "note:123");

    //Execute
    storedNoteRepository.save(note);

    //Validate
    verify(mockSimpleStoredNoteRepository, times(1)).save(eq(note));

  }

  @Test
  public void testFindByConversationId() {
    //Setup
    ConversationId id = buildConversationId(5l);

    //Execute
    storedNoteRepository.findByConversationId(id);

    //Validate
    verify(mockSimpleStoredNoteRepository, times(1)).findByConversationId(eq(id));

  }

  public ConversationId buildConversationId(Long orgCustomerId) {
    ConversationId id = new ConversationId(orgCustomerId, "5558675309", "2228675309",
      "claim:JUNIT");
    return id;
  }
}
