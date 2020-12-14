package com.zipwhip.integration.zipchat.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.zipwhip.message.domain.InboundMessage;
import com.zipwhip.message.utils.MessageUtils;
import java.sql.Date;
import java.time.Instant;
import java.util.LinkedList;
import org.junit.jupiter.api.Test;

public class ConversationMessageTest {

  @Test
  public void testContructFromInboundMessage() {
    //Setup
    InboundMessage msg = new InboundMessage();
    msg.setPayload(new InboundMessage.Payload());
    msg.getPayload().setId(5l);
    msg.getPayload().setFingerPrint(2l);
    msg.getPayload().setDeviceId(8l);
    msg.getPayload().setContactId(10l);
    msg.getPayload().setOperatorId(18l);
    msg.getPayload().setOrgCustomerId(null);
    msg.setOrgCustomerId(187207l);
    msg.getPayload().setBody("This is a text");
    msg.getPayload().setBodySize(msg.getPayload().getBody().length());
    msg.getPayload().setVisible(true);
    msg.getPayload().setIsRead(false);
    msg.getPayload().setDeleted(false);
    msg.getPayload().setHasAttachment(true);
    msg.getPayload().setAttachments(new LinkedList<>());
    msg.getPayload().getAttachments().add(new InboundMessage.Attachment());
    msg.getPayload().setType("MO");
    msg.getPayload().setSourceAddress("+15558675309");
    msg.getPayload().setDestAddress("+12228675309");
    msg.getPayload().setStatusCode(411);
    msg.getPayload().setStatusName("What is the");
    msg.getPayload().setDateCreated(Date.from(Instant.now()));
    msg.getPayload().setContactFirstName("Bob");
    msg.getPayload().setContactLastName("Smith");
    msg.getPayload().setOperatorFirstName("Tom");
    msg.getPayload().setOperatorLastName("Johnson");
    msg.getPayload().setUserFullName("Thomas H. Johnson III Esq");

    //Execute
    ConversationMessage conv = new ConversationMessage(msg);

    //Validate message payloads
    assertEquals(msg.getPayload().getId(), conv.getInboundMessageId());
    assertEquals(msg.getPayload().getFingerPrint(), conv.getFingerPrint());
    assertEquals(msg.getPayload().getDeviceId(), conv.getDeviceId());
    assertEquals(msg.getPayload().getContactId(), conv.getContactId());
    assertEquals(msg.getPayload().getOperatorId(), conv.getOperatorId());
    assertEquals(msg.getOrgCustomerId(), conv.getOrgCustomerId());
    assertEquals(msg.getPayload().getBody(), conv.getOriginalBody());
    assertEquals(msg.getPayload().getBodySize(), conv.getBodySize());
    assertEquals(msg.getPayload().getVisible(), conv.getVisible());
    assertEquals(msg.getPayload().getIsRead(), conv.getIsRead());
    assertEquals(msg.getPayload().getDeleted(), conv.getDeleted());
    assertEquals(msg.getPayload().getHasAttachment(), conv.getHasAttachment());
    assertEquals(msg.getPayload().getAttachments(), conv.getAttachments());
    assertEquals(msg.getPayload().getType(), conv.getType());
    assertEquals(msg.getPayload().getSourceAddress(), conv.getSourceAddress());
    assertEquals(msg.getPayload().getDestAddress(), conv.getDestAddress());
    assertEquals(msg.getPayload().getStatusCode(), conv.getStatusCode());
    assertEquals(msg.getPayload().getStatusName(), conv.getStatusName());
    assertEquals(msg.getPayload().getDateCreated(), conv.getDateCreated());
    assertEquals(msg.getPayload().getContactFirstName(), conv.getContactFirstName());
    assertEquals(msg.getPayload().getContactLastName(), conv.getContactLastName());
    assertEquals(msg.getPayload().getOperatorFirstName(), conv.getOperatorFirstName());
    assertEquals(msg.getPayload().getOperatorLastName(), conv.getOperatorLastName());
    assertEquals(msg.getPayload().getUserFullName(), conv.getUserFullName());

    //Validate simplified fields
    assertEquals(MessageUtils.isInbound(msg), conv.getInbound());
    assertEquals(MessageUtils.formatE164(MessageUtils.getLandline(msg)), conv.getLandlineE164());
    assertEquals(MessageUtils.formatE164(MessageUtils.getPhone(msg)), conv.getMobileE164());
    assertNotNull(conv.getAttachedDocuments());
    assertEquals(0, conv.getAttachedDocuments().size());
  }
}
