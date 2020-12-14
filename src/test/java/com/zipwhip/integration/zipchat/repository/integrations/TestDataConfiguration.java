package com.zipwhip.integration.zipchat.repository.integrations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@Profile("mongo")
@Configuration
@EnableMongoRepositories(basePackages = {"com.zipwhip.integration.zipchat.repository"})
public class TestDataConfiguration {

  @Bean
  public ActiveConversationRepository activeConversationRepository() {
    return new ActiveConversationRepository();
  }

  @Bean
  public ClosedConversationRepository closedConversationRepository() {
    return new ClosedConversationRepository();
  }

  @Bean
  public StagedNoteRepository stagedNoteRepository() {
    return new StagedNoteRepository();
  }

  @Bean
  public StoredNoteRepository storedNoteRepository() {
    return new StoredNoteRepository();
  }

}
