package com.zipwhip.integration.zipchat.controller;

import com.zipwhip.integration.zipchat.entities.OrgConfig;
import com.zipwhip.integration.zipchat.poller.DatahubPoller;
import com.zipwhip.integration.zipchat.repository.OrgConfigRepository;
import com.zipwhip.integration.zipchat.service.SyncServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Private rest controller for performing management functions
 */
@RestController
@RequestMapping(value = "/management")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ManagementController {

  /**
   * A repository for the org configs to use for retrieving and updating configs
   */
  private final OrgConfigRepository configRepo;

  /**
   * A poller to use for forcing processing of a message
   */
  private final DatahubPoller poller;

  /**
   * Sync service which can be used to filter sync targets
   */
  private final SyncServiceImpl syncService;

  /**
   * The ID for the integration
   */
  @Value("${poller.integrationId}")
  private Long integrationId;

  /**
   * Gets the org config for the given OrgCustomerId
   *
   * @param orgCustomerId The orgCustomer for whom to retrive the config
   * @return The OrgCustomerConfig stored in mongo
   */
  @GetMapping(path = "/config/{orgCustomerId}")
  public ResponseEntity<OrgConfig> getOrgConfig(
    @PathVariable(name = "orgCustomerId") Long orgCustomerId) {
    OrgConfig orgConfig = this.configRepo.findById(orgCustomerId).orElse(null);
    //TODO: should 404 if no org config found or is "successfully found null" appropriate
    log.warn("Management call to retrieve orgConfig for orgCustomerId {}: {}", orgCustomerId,
      orgConfig);
    return new ResponseEntity<OrgConfig>(orgConfig, HttpStatus.OK);
  }

  /**
   * Forces an update to the stored config (or creates one if necessary)
   *
   * @param orgCustomerId The orgCustomer for whom to perform the update
   * @param orgConfig     The new config to store for this customer
   * @return The config the customer will be using (after update)
   */
  @PutMapping(path = "/config/{orgCustomerId}")
  public ResponseEntity<?> upsertOrgConfig(@PathVariable(name = "orgCustomerId") Long orgCustomerId,
    @RequestBody OrgConfig orgConfig) {

    if (orgConfig != null && orgConfig.getOrgCustomerId() != null && orgConfig.getOrgCustomerId()
      .equals(orgCustomerId)) {
      OrgConfig existing = this.configRepo.findById(orgCustomerId).orElse(null);
      this.configRepo.save(orgConfig);
      log
        .warn("Management updated config. previous config: {} new config: {}", existing, orgConfig);
      return ResponseEntity.ok("success");
    } else {
      String msg = "Config doesn't match provided orgCustomerId";
      if (orgConfig == null || orgConfig.getOrgCustomerId() == null) {
        msg = "Invalid config supplied";
      }
      return ResponseEntity.badRequest().body(msg);
    }

  }

  /**
   * Deletes the org config for the given OrgCustomerId
   *
   * @param orgCustomerId The orgCustomer for whom to retrive the config
   * @return The OrgCustomerConfig deleted from mongo
   */
  @DeleteMapping(path = "/config/{orgCustomerId}")
  public ResponseEntity<OrgConfig> deleteOrgConfig(
    @PathVariable(name = "orgCustomerId") Long orgCustomerId) {
    OrgConfig orgConfig = this.configRepo.findById(orgCustomerId).orElse(null);
    log.warn("Management call to delete orgConfig for orgCustomerId {}", orgCustomerId);
    if (orgConfig != null) {
      log.warn("Deleting org config: {}", orgConfig);
      this.configRepo.delete(orgConfig);
    }
    return new ResponseEntity<OrgConfig>(orgConfig, HttpStatus.OK);
  }

  /**
   * Not yet implemented. Returns the count of records in kafka for the given orgCustomer which have
   * not yet been processed
   *
   * @param orgCustomerId The orgCustomer to inspect the backlog for
   * @return The number of records not yet processed in kafka for the given customer
   */
  @GetMapping(path = "/kafka/backlogCount")
  public ResponseEntity<Integer> getBacklogCount(
    @RequestParam(name = "orgCustomerId") Long orgCustomerId) {
    return ResponseEntity.ok().body(0);
  }

}
