package com.zipwhip.integration.zipchat.service;

import static com.zipwhip.integration.test.junit.logging.LoggingExtension.assertLogged;
import static com.zipwhip.integration.test.junit.logging.LoggingExtension.assertNotLogged;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import com.zipwhip.integration.bi.service.StatsSenderService;
import com.zipwhip.integration.contactsync.domain.SyncContactPayload;
import com.zipwhip.integration.contactsync.service.ContactSyncService;
import com.zipwhip.integration.zipchat.client.ClaimAPIClient;
import com.zipwhip.integration.zipchat.conversation.IdleTimeConversationWindow;
import com.zipwhip.integration.zipchat.conversation.ImmediateConversationWindow;
import com.zipwhip.integration.zipchat.conversation.format.ContactFieldFormatter;
import com.zipwhip.integration.zipchat.domain.ConversationMessage;
import com.zipwhip.integration.zipchat.domain.SyncTargets;
import com.zipwhip.integration.zipchat.entities.OrgConfig;
import com.zipwhip.integration.zipchat.stub.acc.ZipwhipClaimContact;
import com.zipwhip.integration.zipchat.stub.acc.ZipwhipClaimContact.ClaimUsers;
import com.zipwhip.integration.zipchat.stub.acc.ZipwhipClaimContact.RoleNames;
import com.zipwhip.integration.zipchat.stub.acc.ZipwhipClaimUser;
import com.zipwhip.integration.test.junit.logging.LoggingExtension;
import com.zipwhip.message.domain.InboundContact;
import com.zipwhip.message.domain.InboundMessage;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@ExtendWith(LoggingExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@Slf4j
public class SyncServiceImplTest {

  @Mock
  private ClaimAPIClient mockClaimService;

  @Mock
  private ZipwhipAcceleratorAPIClient mockAccService;

  @Mock
  private StatsSenderService mockStatsSenderService;

  @Mock
  private ContactSyncService mockContactSyncService;

  @Mock
  private ActiveConversationRepository mockConvoRepository;

  @Mock
  private ConversationClosingService mockConvoClosingService;

  @Mock
  private AttachmentService mockAttachmentService;

  @InjectMocks
  private SyncServiceImpl service;

  private InboundMessage injectableMessage;

  private InboundContact injectableContact;

  private OrgConfig orgConfig;

  private List<ZipwhipClaimContact> crmContactList;

  @Captor
  private ArgumentCaptor<Set<String>> claimPublicIdSetCaptor;

  @Captor
  private ArgumentCaptor<String> claimPublicIdCaptor;

  @BeforeEach
  public void setup() {

    crmContactList = new LinkedList<>();

    orgConfig = new OrgConfig();
    orgConfig.setConversationWindow(new ImmediateConversationWindow());
    orgConfig.setSyncParams(new SyncParams());
    orgConfig.getSyncParams().setContactFields(new FormattedContactFields());
    orgConfig.getSyncParams().getContactFields()
      .setCustomField1(ContactFieldFormatter.CLAIM_NUMBER_LIST);
    orgConfig.setContactSyncFromCrmEnabled(true);
    orgConfig.setMessageArchiveToCrmEnabled(true);
    injectableMessage = new InboundMessage();
    injectableContact = new InboundContact();

    doReturn(crmContactList).when(mockAccService)
      .findByPhone(any(String.class), any(OrgConfig.class));
  }

  @Test
  public void testProcessMessage() {
    //Setup
    String body = "this is a text body";
    String mobile = "+15558675309";
    String landline = "+14328675309";
    injectableMessage.setPayload(new InboundMessage.Payload());
    injectableMessage.getPayload().setBody(body);
    injectableMessage.getPayload().setType("MO");
    injectableMessage.getPayload().setSourceAddress(mobile);
    injectableMessage.getPayload().setDestAddress(landline);
    crmContactList.add(new ZipwhipClaimContact());
    crmContactList.get(0).setClaimAdjusterPhoneNumber(landline);
    crmContactList.get(0).setPrimaryPhoneNumber(mobile);
    crmContactList.get(0).setClaimPublicId("GOOD_PUBLIC_ID");
    crmContactList.add(new ZipwhipClaimContact());
    crmContactList.get(1).setClaimAdjusterPhoneNumber("+16874852043");
    crmContactList.get(1).setPrimaryPhoneNumber(mobile);
    crmContactList.get(1).setClaimPublicId("BAD_PUBLIC_ID");
    ConversationMessage expectedMessage = new ConversationMessage(injectableMessage);

    //Execute
    service.processMessage(injectableMessage, orgConfig);

    //Validate
    verify(mockAccService, times(1)).findByPhone(eq(mobile), eq(orgConfig));
    verify(mockClaimService, times(1))
      .createNote(eq(expectedMessage), claimPublicIdCaptor.capture(), eq(orgConfig));
    assertEquals("GOOD_PUBLIC_ID", claimPublicIdCaptor.getValue());
    assertNotLogged("Contact sync feature is disabled by user");
  }

  @Test
  public void testSyncContactToZipwhipMobileMatchFound() {
    //Setup
    String body = "this is a text body";
    String mobile = "+15558675309";
    String landline = "+14328675309";
    injectableMessage.setPayload(new InboundMessage.Payload());
    injectableMessage.getPayload().setBody(body);
    injectableMessage.getPayload().setType("MO");
    injectableMessage.getPayload().setSourceAddress(mobile);
    injectableMessage.getPayload().setDestAddress(landline);
    injectableMessage.getPayload().setId(234l);
    crmContactList.add(new ZipwhipClaimContact());
    crmContactList.get(0).setClaimAdjusterPhoneNumber(landline);
    crmContactList.get(0).setPrimaryPhoneNumber("+12228675309");
    crmContactList.get(0).setClaimNumber("wrongMobileClaim1");
    crmContactList.add(new ZipwhipClaimContact());
    crmContactList.get(1).setClaimAdjusterPhoneNumber(landline);
    crmContactList.get(1).setPrimaryPhoneNumber(mobile);
    crmContactList.get(1).setClaimNumber("goodMobilesClaimNum");

    //Execute
    service.processMessage(injectableMessage, orgConfig);

    //Validate
    ArgumentCaptor<SyncContactPayload> captor = ArgumentCaptor.forClass(SyncContactPayload.class);
    verify(mockContactSyncService, times(1)).syncToZipwhip(captor.capture());
    assertNotNull(captor.getValue().getRequestId());
    assertNotNull(captor.getValue().getRequestDateTime());
    assertEquals("234", captor.getValue().getTriggeringMessageId());
    assertTrue(captor.getValue().getCustomField1()
      .contains("wrongMobileClaim1")); //No filter now let's them all through
    assertEquals("wrongMobileClaim1, goodMobilesClaimNum", captor.getValue().getCustomField1());
  }

  @Test
  public void testSyncContactToZipwhipPrimaryAdjuster() {
    //Setup
    String body = "this is a text body";
    String mobile = "+15558675309";
    String landline = "+14328675309";
    injectableMessage.setPayload(new InboundMessage.Payload());
    injectableMessage.getPayload().setBody(body);
    injectableMessage.getPayload().setType("MO");
    injectableMessage.getPayload().setSourceAddress(mobile);
    injectableMessage.getPayload().setDestAddress(landline);
    crmContactList.add(new ZipwhipClaimContact());
    crmContactList.get(0).setClaimAdjusterPhoneNumber(landline);
    crmContactList.get(0).setPrimaryPhoneNumber("+12228675309");
    crmContactList.get(0).setClaimLossDate(XMLGregorianCalendarImpl.createDate(1984, 1, 1, 0));
    crmContactList.get(0).setClaimNumber("Claim2");
    crmContactList.add(new ZipwhipClaimContact());
    crmContactList.get(1).setClaimAdjusterPhoneNumber(landline);
    crmContactList.get(1).setClaimLossDate(XMLGregorianCalendarImpl.createDate(1990, 1, 1, 0));
    crmContactList.get(1).setPrimaryPhoneNumber("+12228675309");
    crmContactList.get(1).setClaimNumber("Claim1");
    RoleNames roleNames = new RoleNames();
    roleNames.getEntry().add("adjuster");
    crmContactList.get(1).setRoleNames(roleNames);

    //Execute
    SyncTargets syncTargets = service.extractSyncTargets(crmContactList, landline, mobile);

    //Validate
    assertEquals("Claim1",
      syncTargets.getContactSyncCandidates().iterator().next().getClaimNumber());
  }

  @Test
  public void testSyncContactToZipwhipclaimUsers() {
    //Setup
    String body = "this is a text body";
    String mobile = "+15558675309";
    String landline = "+14328675309";
    injectableMessage.setPayload(new InboundMessage.Payload());
    injectableMessage.getPayload().setBody(body);
    injectableMessage.getPayload().setType("MO");
    injectableMessage.getPayload().setSourceAddress(mobile);
    injectableMessage.getPayload().setDestAddress(landline);
    crmContactList.add(new ZipwhipClaimContact());
    crmContactList.get(0).setClaimAdjusterPhoneNumber(landline);
    crmContactList.get(0).setPrimaryPhoneNumber("+12228675309");
    crmContactList.get(0).setClaimLossDate(XMLGregorianCalendarImpl.createDate(1984, 1, 1, 0));
    crmContactList.get(0).setClaimNumber("Claim2");
    crmContactList.add(new ZipwhipClaimContact());
    crmContactList.get(1).setClaimAdjusterPhoneNumber(landline);
    crmContactList.get(1).setClaimUsers(new ClaimUsers());
    crmContactList.get(1).setClaimLossDate(XMLGregorianCalendarImpl.createDate(1990, 1, 1, 0));
    crmContactList.get(1).setPrimaryPhoneNumber("+12228675309");
    crmContactList.get(1).setClaimNumber("Claim1");
    RoleNames roleNames = new RoleNames();
    roleNames.getEntry().add("adjuster");
    crmContactList.get(1).setRoleNames(roleNames);

    //Execute
    SyncTargets syncTargets = service.extractSyncTargets(crmContactList, landline, mobile);

    //Validate
    assertEquals("Claim1",
      syncTargets.getContactSyncCandidates().iterator().next().getClaimNumber());
  }

  @Test
  public void testSyncContactToZipwhipclaimUsersMatch() {
    //Setup
    String body = "this is a text body";
    String mobile = "+15558675309";
    String landline = "+14328675309";
    injectableMessage.setPayload(new InboundMessage.Payload());
    injectableMessage.getPayload().setBody(body);
    injectableMessage.getPayload().setType("MO");
    injectableMessage.getPayload().setSourceAddress(mobile);
    injectableMessage.getPayload().setDestAddress(landline);
    crmContactList.add(new ZipwhipClaimContact());
    crmContactList.get(0).setClaimAdjusterPhoneNumber("+19228675309");
    crmContactList.get(0).setPrimaryPhoneNumber("+19228675309");
    crmContactList.get(0).setClaimLossDate(XMLGregorianCalendarImpl.createDate(1984, 1, 1, 0));
    crmContactList.get(0).setClaimNumber("Claim2");
    crmContactList.add(new ZipwhipClaimContact());
    crmContactList.get(1).setClaimAdjusterPhoneNumber(landline);
    ClaimUsers claimUsers = new ClaimUsers();
    ZipwhipClaimUser zipwhipClaimUser = new ZipwhipClaimUser();
    zipwhipClaimUser.setUserPhoneNumber("14328675309");
    claimUsers.getEntry().add(zipwhipClaimUser);
    crmContactList.get(0).setClaimUsers(claimUsers);
    crmContactList.get(1).setClaimLossDate(XMLGregorianCalendarImpl.createDate(1990, 1, 1, 0));
    crmContactList.get(1).setPrimaryPhoneNumber("+19228675309");
    crmContactList.get(1).setClaimNumber("Claim1");
    RoleNames roleNames = new RoleNames();
    roleNames.getEntry().add("adjuster");
    crmContactList.get(1).setRoleNames(roleNames);

    //Execute
    SyncTargets syncTargets = service.extractSyncTargets(crmContactList, landline, mobile);

    //Validate
    assertEquals("Claim1",
      syncTargets.getContactSyncCandidates().iterator().next().getClaimNumber());
  }

  @Test
  public void testSyncContactToZipwhipNoMobileMatchesFound() {
    //Setup
    String body = "this is a text body";
    String mobile = "+15558675309";
    String landline = "+14328675309";
    injectableMessage.setPayload(new InboundMessage.Payload());
    injectableMessage.getPayload().setBody(body);
    injectableMessage.getPayload().setType("MO");
    injectableMessage.getPayload().setSourceAddress(mobile);
    injectableMessage.getPayload().setDestAddress(landline);
    crmContactList.add(new ZipwhipClaimContact());
    crmContactList.get(0).setClaimAdjusterPhoneNumber(landline);
    crmContactList.get(0).setPrimaryPhoneNumber("+12228675309");
    crmContactList.get(0).setClaimLossDate(XMLGregorianCalendarImpl.createDate(1984, 1, 1, 0));
    crmContactList.get(0).setClaimNumber("wrongMobileClaim2");
    crmContactList.add(new ZipwhipClaimContact());
    crmContactList.get(1).setClaimAdjusterPhoneNumber(landline);
    crmContactList.get(1).setClaimLossDate(XMLGregorianCalendarImpl.createDate(1990, 1, 1, 0));
    crmContactList.get(1).setPrimaryPhoneNumber("+12228675309");
    crmContactList.get(1).setClaimNumber("wrongMobileClaim1");

    //Execute
    service.processMessage(injectableMessage, orgConfig);

    //Validate
    ArgumentCaptor<SyncContactPayload> captor = ArgumentCaptor.forClass(SyncContactPayload.class);
    verify(mockContactSyncService, times(1)).syncToZipwhip(captor.capture());
    assertEquals("wrongMobileClaim1, wrongMobileClaim2", captor.getValue().getCustomField1());
  }

  @Test
  public void testProcessMessageForExistingConversation() {
    //Setup
    orgConfig.setConversationWindow(new IdleTimeConversationWindow());
    String body = "this is a text body";
    String mobile = "+15558675309";
    String landline = "+14328675309";
    injectableMessage.setPayload(new InboundMessage.Payload());
    injectableMessage.getPayload().setBody(body);
    injectableMessage.getPayload().setType("MO");
    injectableMessage.getPayload().setSourceAddress(mobile);
    injectableMessage.getPayload().setDestAddress(landline);
    injectableMessage.getPayload().setDateCreated(Date.from(Instant.EPOCH));
    crmContactList.add(new ZipwhipClaimContact());
    crmContactList.get(0).setClaimAdjusterPhoneNumber(landline);
    crmContactList.get(0).setClaimPublicId("GOOD_PUBLIC_ID");
    crmContactList.add(new ZipwhipClaimContact());
    crmContactList.get(1).setClaimAdjusterPhoneNumber("+16874852043");
    crmContactList.get(1).setClaimPublicId("BAD_PUBLIC_ID");
    ConversationId conversationId = new ConversationId(orgConfig.getOrgCustomerId(), landline,
      mobile, "GOOD_PUBLIC_ID");
    ActiveConversation existingConvo = new ActiveConversation(conversationId);
    doReturn(Optional.of(existingConvo)).when(mockConvoRepository)
      .findById(any(ConversationId.class));

    //Execute
    service.processMessage(injectableMessage, orgConfig);

    //Validate
    verify(mockConvoRepository).save(eq(existingConvo));

  }

  @Test
  public void testProcessMessageForNewConversation() {
    //Setup
    orgConfig.setConversationWindow(new IdleTimeConversationWindow());
    String body = "this is a text body";
    String mobile = "+15558675309";
    String landline = "+14328675309";
    injectableMessage.setPayload(new InboundMessage.Payload());
    injectableMessage.getPayload().setBody(body);
    injectableMessage.getPayload().setType("MO");
    injectableMessage.getPayload().setSourceAddress(mobile);
    injectableMessage.getPayload().setDestAddress(landline);
    injectableMessage.getPayload().setDateCreated(Date.from(Instant.EPOCH));
    crmContactList.add(new ZipwhipClaimContact());
    crmContactList.get(0).setClaimAdjusterPhoneNumber(landline);
    crmContactList.get(0).setClaimPublicId("GOOD_PUBLIC_ID");
    crmContactList.add(new ZipwhipClaimContact());
    crmContactList.get(1).setClaimAdjusterPhoneNumber("+16874852043");
    crmContactList.get(1).setClaimPublicId("BAD_PUBLIC_ID");
    doReturn(Optional.empty()).when(mockConvoRepository).findById(any(ConversationId.class));

    //Execute
    service.processMessage(injectableMessage, orgConfig);

    //Validate
    verify(mockConvoRepository, times(1)).save(any(ActiveConversation.class));

  }

  @Test
  public void testProcessContact() {
    //Setup
    String first = "Steve";
    String last = "Stephenson";
    String mobile = "+15558675309";
    injectableContact.setPayload(new InboundContact.Payload());
    injectableContact.getPayload().setPhoneNumber(mobile);
    injectableContact.getPayload().setFirstName(first);
    injectableContact.getPayload().setLastName(last);

    //Execute
    service.processContact(injectableContact, orgConfig);

    //Validate
    assertLogged("No-Op Handling saved contact: " + last + ", " + first + "; " + mobile);
  }

  @Test
  public void testProcessMessageContactSyncDisabled() {
    //Setup
    String body = "this is a text body";
    String mobile = "+15558675309";
    String landline = "+14328675309";
    injectableMessage.setPayload(new InboundMessage.Payload());
    injectableMessage.getPayload().setBody(body);
    injectableMessage.getPayload().setType("MO");
    injectableMessage.getPayload().setSourceAddress(mobile);
    injectableMessage.getPayload().setDestAddress(landline);
    crmContactList.add(new ZipwhipClaimContact());
    crmContactList.get(0).setClaimAdjusterPhoneNumber(landline);
    crmContactList.get(0).setPrimaryPhoneNumber(mobile);
    crmContactList.get(0).setClaimPublicId("GOOD_PUBLIC_ID");
    crmContactList.add(new ZipwhipClaimContact());
    crmContactList.get(1).setClaimAdjusterPhoneNumber("+16874852043");
    crmContactList.get(1).setPrimaryPhoneNumber(mobile);
    crmContactList.get(1).setClaimPublicId("BAD_PUBLIC_ID");
    ConversationMessage expectedMessage = new ConversationMessage(injectableMessage);
    orgConfig.setContactSyncFromCrmEnabled(false);

    //Execute
    service.processMessage(injectableMessage, orgConfig);

    //Validate
    verify(mockAccService, times(1)).findByPhone(eq(mobile), eq(orgConfig));
    verifyZeroInteractions(mockContactSyncService);
    verify(mockClaimService, times(1))
      .createNote(eq(expectedMessage), claimPublicIdCaptor.capture(), eq(orgConfig));
    assertEquals("GOOD_PUBLIC_ID", claimPublicIdCaptor.getValue());
    assertLogged("Contact Sync feature is disabled by user");
  }

  @Test
  public void testProcessMessageMessageArchiveDisabled() {
    //Setup
    String body = "this is a text body";
    String mobile = "+15558675309";
    String landline = "+14328675309";
    injectableMessage.setPayload(new InboundMessage.Payload());
    injectableMessage.getPayload().setBody(body);
    injectableMessage.getPayload().setType("MO");
    injectableMessage.getPayload().setSourceAddress(mobile);
    injectableMessage.getPayload().setDestAddress(landline);
    crmContactList.add(new ZipwhipClaimContact());
    crmContactList.get(0).setClaimAdjusterPhoneNumber(landline);
    crmContactList.get(0).setPrimaryPhoneNumber(mobile);
    crmContactList.get(0).setClaimPublicId("GOOD_PUBLIC_ID");
    crmContactList.add(new ZipwhipClaimContact());
    crmContactList.get(1).setClaimAdjusterPhoneNumber("+16874852043");
    crmContactList.get(1).setPrimaryPhoneNumber(mobile);
    crmContactList.get(1).setClaimPublicId("BAD_PUBLIC_ID");
    ConversationMessage expectedMessage = new ConversationMessage(injectableMessage);
    orgConfig.setMessageArchiveToCrmEnabled(false);

    //Execute
    service.processMessage(injectableMessage, orgConfig);

    //Validate
    verify(mockAccService, times(1)).findByPhone(eq(mobile), eq(orgConfig));
    verify(mockClaimService, never())
      .createNote(eq(expectedMessage), any(String.class), eq(orgConfig));
    verifyZeroInteractions(mockAttachmentService);
    verifyZeroInteractions(mockConvoRepository);
    assertLogged("Message Archive feature is disabled by user");
  }

  @Test
  public void testProcessMessageMessageArchiveAndContactSyncDisabled() {
    //Setup
    String body = "this is a text body";
    String mobile = "+15558675309";
    String landline = "+14328675309";
    injectableMessage.setPayload(new InboundMessage.Payload());
    injectableMessage.getPayload().setBody(body);
    injectableMessage.getPayload().setType("MO");
    injectableMessage.getPayload().setSourceAddress(mobile);
    injectableMessage.getPayload().setDestAddress(landline);
    crmContactList.add(new ZipwhipClaimContact());
    crmContactList.get(0).setClaimAdjusterPhoneNumber(landline);
    crmContactList.get(0).setPrimaryPhoneNumber(mobile);
    crmContactList.get(0).setClaimPublicId("GOOD_PUBLIC_ID");
    crmContactList.add(new ZipwhipClaimContact());
    crmContactList.get(1).setClaimAdjusterPhoneNumber("+16874852043");
    crmContactList.get(1).setPrimaryPhoneNumber(mobile);
    crmContactList.get(1).setClaimPublicId("BAD_PUBLIC_ID");
    ConversationMessage expectedMessage = new ConversationMessage(injectableMessage);
    orgConfig.setContactSyncFromCrmEnabled(false);
    orgConfig.setMessageArchiveToCrmEnabled(false);

    //Execute
    service.processMessage(injectableMessage, orgConfig);

    //Validate
    verifyZeroInteractions(mockAccService);
    verifyZeroInteractions(mockClaimService);
    verifyZeroInteractions(mockAttachmentService);
    verifyZeroInteractions(mockConvoRepository);
    assertLogged("Contact Sync and Message Archive feature is disabled by user");
  }

  public boolean findLog(List<ILoggingEvent> list, String searchFor) {
    boolean retval = false;
    StringBuilder notMatchedLines = new StringBuilder();
    String div = "";
    for (ILoggingEvent item : list) {
      String line = item.getFormattedMessage();
      if (line.contains(searchFor)) {
        retval = true;
      } else {
        notMatchedLines.append(div);
        notMatchedLines.append(line);
        div = "\n\t";
      }
    }
    log.info("{} not found in any lines: {}", searchFor, notMatchedLines);
    return retval;
  }

}
