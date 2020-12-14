package com.zipwhip.integration.zipchat.error;

/**
 * A simple runtime exception for expressing that an action requiring an OrgConfig has failed to
 * find an OrgConfig and is likely to fail (unless it can handle this exception)
 */
public class NoOrgConfigException extends RuntimeException {

  /**
   * Constructs an exception with the message indicating that no org config has been located for the
   * orgCustomer for a given integrationId
   *
   * @param orgCustomerId The orgCustomer who lacks a configuration
   * @param integrationId The integration they don't have a config for
   */
  public NoOrgConfigException(Long orgCustomerId, Long integrationId) {
    super(String
      .format("No OrgConfig found for orgCustomerId: %d in integration: %d", orgCustomerId,
        integrationId));

  }

  /**
   * Constructs an exception with the message indicating that no org config has been located for the
   * orgCustomer for a given integrationId with a wrapped cause
   *
   * @param orgCustomerId The orgCustomer who lacks a configuration
   * @param integrationId The integration they don't have a config for
   */
  public NoOrgConfigException(Long orgCustomerId, Long integrationId, Throwable cause) {
    super(String
      .format("No OrgConfig found for orgCustomerId: %d in integration: %d; caused by: %s",
        orgCustomerId, integrationId, cause.getMessage()), cause);

  }
}
