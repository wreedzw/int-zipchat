package com.zipwhip.integration.zipchat.repository;

import com.zipwhip.integration.zipchat.entities.OrgConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * A repository of configurations for orgCustomers
 */
public interface OrgConfigRepository extends MongoRepository<OrgConfig, Long> {

}
