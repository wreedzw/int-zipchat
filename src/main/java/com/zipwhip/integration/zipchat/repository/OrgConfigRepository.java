package com.zipwhip.integration.zipchat.repository;

import com.zipwhip.integration.zipchat.entities.OrgConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * A repository of configurations for orgCustomers
 */
public interface OrgConfigRepository extends MongoRepository<OrgConfig, Long> {

  /**
   * Finds an orgConfig with the matching token
   *
   * @param zipwhipToken The token to use as the criteria
   * @return The orgConfig, if found
   */
  OrgConfig findByZipwhipToken(String zipwhipToken);

}
