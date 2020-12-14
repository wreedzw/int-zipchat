package com.zipwhip.integration.zipchat.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import com.zipwhip.integration.zipchat.stub.acc.ZipwhipClaimContact;
import org.junit.jupiter.api.Test;

public class SyncTargetsTest {

  @Test
  public void testToString() {

    SyncTargets targets = new SyncTargets();

    assertEquals("SyncTargets(contactSyncCandidates=[], messageSyncTargets=[])",
      targets.toString());
  }

  @Test
  public void testSortedInsert() {
    //Setup
    SyncTargets targets = new SyncTargets();
    ZipwhipClaimContact c1 = new ZipwhipClaimContact();
    c1.setClaimLossDate(XMLGregorianCalendarImpl.createDate(1980, 1, 1, 0));
    ZipwhipClaimContact c2 = new ZipwhipClaimContact();
    c2.setClaimLossDate(XMLGregorianCalendarImpl.createDate(1984, 1, 1, 0));
    ZipwhipClaimContact c3 = new ZipwhipClaimContact();
    c3.setClaimLossDate(XMLGregorianCalendarImpl.createDate(1990, 1, 1, 0));

    //Execute
    targets.addContactSyncCandidate(c2);
    targets.addContactSyncCandidate(c1);
    targets.addContactSyncCandidate(c3);

    //Validate
    assertEquals(new ContactSyncCandidate(c1), targets.getContactSyncCandidates().get(2));
    assertEquals(new ContactSyncCandidate(c2), targets.getContactSyncCandidates().get(1));
    assertEquals(new ContactSyncCandidate(c3), targets.getContactSyncCandidates().get(0));
  }
}
