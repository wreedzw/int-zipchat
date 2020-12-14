package com.zipwhip.integration.zipchat.repository.integrations;

import static org.assertj.core.api.Assertions.assertThat;

import com.zipwhip.integration.zipchat.entities.OrgConfig;
import com.zipwhip.integration.zipchat.repository.OrgConfigRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ActiveProfiles("mongo")
@DataMongoTest
@ContextConfiguration(classes = TestDataConfiguration.class)
public class OrgConfigTest {

  @Autowired
  private OrgConfigRepository orgConfigRepository;

  @Test
  public void testDefaultsOnRead() {
    OrgConfig originalOrgConfig = new OrgConfig();
    originalOrgConfig.setOrgCustomerId(20L);
    originalOrgConfig.setContactSyncFromCrmEnabled(null);
    originalOrgConfig.setMessageArchiveToCrmEnabled(null);
    orgConfigRepository.insert(originalOrgConfig);

    final Optional<OrgConfig> actOrgConfig = orgConfigRepository
      .findById(originalOrgConfig.getOrgCustomerId());
    assertThat(actOrgConfig.isPresent()).isTrue();
    assertThat(actOrgConfig.get().getContactSyncFromCrmEnabled()).isTrue();
    assertThat(actOrgConfig.get().getMessageArchiveToCrmEnabled()).isTrue();
    assertThat(actOrgConfig.get().getTextFromCrmEnabled()).isTrue();
  }

  @Test
  public void testStoredOnRead() {
    OrgConfig originalOrgConfig = new OrgConfig();
    originalOrgConfig.setOrgCustomerId(22L);
    originalOrgConfig.setContactSyncFromCrmEnabled(Boolean.FALSE);
    originalOrgConfig.setMessageArchiveToCrmEnabled(null);
    orgConfigRepository.insert(originalOrgConfig);

    final Optional<OrgConfig> actOrgConfig = orgConfigRepository
      .findById(originalOrgConfig.getOrgCustomerId());
    assertThat(actOrgConfig.isPresent()).isTrue();
    assertThat(actOrgConfig.get().getContactSyncFromCrmEnabled()).isFalse();
    assertThat(actOrgConfig.get().getMessageArchiveToCrmEnabled()).isTrue();
    assertThat(actOrgConfig.get().getTextFromCrmEnabled()).isTrue();
  }
}
