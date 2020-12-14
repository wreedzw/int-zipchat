package com.zipwhip.integration.zipchat.repository.integrations;

import com.zipwhip.integration.zipchat.domain.AttachedDocument;
import com.zipwhip.integration.zipchat.domain.ConversationMessage;

import java.util.ArrayList;
import java.util.List;

class RepositoryTestUtils {

  static final long DOCUMENT_ID = 1;

  static final long ORG_CUSTOMER_ID = 5;

  static final String CLAIM_PUBLIC_ID = "claim:1";

  static final String DOCUMENT_PUBLIC_ID = "docPublicId:1";

  static final String NOTE_PUBLIC_ID = "note:1";

  static final String UNKNOWN_DATE_FORMAT = "20191812";

  static final long START_TIME = 1546293600000L;              //2019-01-01 00:00:00 in epoch millis

  static final long END_TIME = 1548972000000L;                //2019-02-01 00:00:00 in epoch milli

  static final long FIRST_MESSAGE_TIME = 1546297200000L;      //2019-01-01 01:00:00 in epoch millis

  static final long LAST_MESSAGE_TIME = 1546300800000L;       //2019-02-01 02:00:00 in epoch millis

  static final long MESSAGE_TIME = 1548972001000L;            //2019-02-01 00:00:01 in epoch millis

  static List<ConversationMessage> buildConversationMessages() {
    final List<ConversationMessage> messages = new ArrayList<>();
    final ConversationMessage message = new ConversationMessage();
    message.setAttachedDocuments(buildAttachedDocuments());
    messages.add(message);

    return messages;
  }

  static List<AttachedDocument> buildAttachedDocuments() {
    final List<AttachedDocument> documents = new ArrayList<>();
    final AttachedDocument document = new AttachedDocument();
    document.setDocumentId(DOCUMENT_ID);
    document.setDocumentPublicId(DOCUMENT_PUBLIC_ID);
    documents.add(document);

    return documents;
  }

  static ConversationWindow buildConversationWindow() {
    final ConversationWindow window = new ConversationWindow();
    window.setFirstMessage(FIRST_MESSAGE_TIME);
    window.setLastMessage(LAST_MESSAGE_TIME);

    return window;
  }
}
