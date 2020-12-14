package com.zipwhip.integration.zipchat.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.zipwhip.message.domain.InboundMessage;
import org.junit.jupiter.api.Test;

public class AttachedDocumentTest {

  @Test
  public void testContructor() {
    //Setup
    InboundMessage.Attachment attachment = new InboundMessage.Attachment();
    attachment.setMimeType("image/jpg");

    //Execute
    AttachedDocument doc = new AttachedDocument("iRuleThe.jpg", 4l, "doc:123",
      attachment, "wefhijubiubfh", 1, new AttachedDocument.AttachmentContext());

    //Validate
    assertEquals(
      "AttachedDocument(fileName=iRuleThe.jpg, documentId=4, documentPublicId=doc:123, " +
        "rawAttachment=InboundMessage.Attachment(storageKey=null, secureKey=null, mimeType=image/jpg), "
        +
        "encodedAttachmentId=wefhijubiubfh, attachmentNumber=1, " +
        "context=AttachedDocument.AttachmentContext(messageId=null, magnitudeOfAttachments=0))",
      doc.toString());

  }

  @Test
  public void testContructorVsSetters() {
    //Setup
    InboundMessage.Attachment attachment = new InboundMessage.Attachment();
    attachment.setMimeType("image/jpg");
    AttachedDocument doc1 = new AttachedDocument("iRuleThe.jpg", 4l, "doc:123",
      attachment, "wefhijubiubfh", 1, new AttachedDocument.AttachmentContext());
    AttachedDocument doc2 = new AttachedDocument();
    doc2.setFileName("iRuleThe.jpg");
    doc2.setDocumentId(4l);
    doc2.setDocumentPublicId("doc:123");
    doc2.setRawAttachment(attachment);
    doc2.setEncodedAttachmentId("wefhijubiubfh");
    doc2.setAttachmentNumber(1);
    doc2.setContext(new AttachedDocument.AttachmentContext());

    //Execute

    //Validate
    assertEquals(doc1, doc2);

  }

  @Test
  public void testDefaultContructor() {
    //Setup

    //Execute
    AttachedDocument doc = new AttachedDocument();

    //Validate
    assertEquals("AttachedDocument(fileName=null, documentId=null, documentPublicId=null, " +
        "rawAttachment=null, encodedAttachmentId=null, attachmentNumber=null, context=null)",
      doc.toString());

  }
}
