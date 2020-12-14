package com.zipwhip.integration.zipchat.install;

import com.zipwhip.subscription.client.SubscriptionInfoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller to perform the uninstall of any users wishing to remove this integration
 */
@Controller
@RequestMapping("/secure/uninstall")
public class UninstallController {

  /**
   * A client to invoke int-manager to trigger the uninstall
   */
  @Autowired
  private SubscriptionInfoClient subscriptionInfoClient;


  /**
   * This integrations ID
   */
  @Value("${poller.integrationId}")
  private Long integrationId;

  /**
   * Performs the uninstall then redirects the user to the install page for this integration
   *
   * @param orgId The orgId to uninstall
   * @return A redirect message pointing them to the integrations install page
   */
  @PostMapping("/orgId/{orgId}")
  public String uninstall(@PathVariable Long orgId) {
    subscriptionInfoClient.uninstall(orgId, integrationId);
    return "redirect:/secure/install/details/orgId/" + orgId;
  }

}
