package com.zipwhip.integration.zipchat.install;

import com.zipwhip.controller.utils.ControllerUtils;
import com.zipwhip.integration.zipchat.entities.OrgConfig;
import com.zipwhip.integration.zipchat.error.NoOrgConfigException;
import com.zipwhip.integration.zipchat.repository.OrgConfigRepository;
import com.zipwhip.integration.zipchat.service.InstallService;
import com.zipwhip.integration.zipchat.domain.*;
import com.zipwhip.legacy.config.AccountConsoleConfig;
import com.zipwhip.logging.CompletionCode;
import com.zipwhip.logging.IntegrationFeature;
import com.zipwhip.logging.MDC;
import com.zipwhip.logging.MDCFields;
import com.zipwhip.logging.MDCUtil;
import com.zipwhip.subscription.client.SubscriptionInfoClient;
import com.zipwhip.subscription.domain.SubscriptionPayload;
import com.zipwhip.subscription.domain.SubscriptionResponse;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/** This controller manages the install and settings for the ZipChat integration */
@Controller
@RequestMapping(value = "/secure/install")
@Slf4j
public class InstallController {

  /** The ID for the integration */
  @Value("${poller.integrationId}")
  @Setter
  private Long integrationId;

  /** The client for getting and creating install records */
  @Autowired private SubscriptionInfoClient subscriptionInfoClient;

  /** A repository for managing configurations of orgCustomers */
  @Autowired private OrgConfigRepository orgConfigRepository;

  /** An account console management object to perform redirects to the iframe'd experience */
  @Autowired private AccountConsoleConfig accountConsoleConfig;

  /** The service for processing controller logic */
  @Autowired private InstallService installService;

  private static final String INTEGRATION_URL = "/int-zipchat-service";

  /**
   * Loads the details view for a given orgCustomer
   *
   * @param orgCustomerId The orgCustomer Id for whom to load the view
   * @param modelMap      The modelMap of the client
   * @return The name of the view
   */
  @GetMapping("/details/orgId/{orgCustomerId}")
  public String details(@PathVariable Long orgCustomerId, ModelMap modelMap) {

    modelMap.addAttribute("orgId", orgCustomerId);

    return "details";
  }

  /**
   * Loads the settings view for a given org customer
   *
   * @param orgCustomerId The orgCustomer Id for whom to load the view
   * @param modelMap      The modelMap of the client
   * @return The name of the view
   */
  @GetMapping("/settings/orgId/{orgCustomerId}")
  public String settings(@PathVariable Long orgCustomerId, ModelMap modelMap) {
    final SettingsConfig viewConfig = installService.getSettingsConfig(orgCustomerId);

    modelMap.addAttribute("orgCustomerId", orgCustomerId);
    modelMap.addAttribute("params", viewConfig);

    return "settings";
  }


  /**
   * Loads the view for performing authentication
   *
   * @param orgCustomerId The orgCustomer Id for whom to load the view
   * @param reconnect     boolean indicating if this is a reconnect attempt (true) or a fresh
   *                      install (false)
   * @param modelMap      The modelMap of the client
   * @return The name of the view
   */
  @RequestMapping(
      value = "/authenticate/orgId/{orgCustomerId}",
      method = {
    RequestMethod.POST, // Support forwarded POST support
    RequestMethod.GET   // Allow redirects (obsolete?)
  })
  public String authenticate(
      @PathVariable Long orgCustomerId,
      @RequestParam(value = "reconnect", required = false, defaultValue = "false")
          Boolean reconnect,
    ModelMap modelMap) {
    MDCUtil.startFeature(reconnect ? IntegrationFeature.CONFIG : IntegrationFeature.INSTALL);
    MDC.put(MDCFields.ORG_CUSTOMER_ID, orgCustomerId);
    MDC.put(MDCFields.FEATURE_STEP, "authenticate_init");
    MDC.put(MDCFields.INT_ID, integrationId);

    try {
      populateModel(integrationId, modelMap);
      modelMap.addAttribute("orgCustomerId", orgCustomerId);
      modelMap.addAttribute("reconnect", reconnect);
      MDCUtil.endFeature(CompletionCode.SUCCESS);
    } finally {
      MDC.clear();
    }
    return "authenticate";
  }

  /**
   * Entry point for install, forward the users request to the authentication page
   *
   * @param orgCustomerId The orgCustomer that is performing the install
   * @param installedBy   The name of the user (not username) performing the install
   * @param reconnect     boolean indicating if this is a reconnect attempt (true) or a fresh
   *                      install (false)
   * @param request       The servlet request being made
   * @param response      The servlet response to populate
   * @return URL forward for performing authentication
   */
  @RequestMapping(value = "/orgId/{orgCustomerId}", method = RequestMethod.POST)
  public String install(@PathVariable Long orgCustomerId,
    @RequestParam(required = false, value = "installedBy") String installedBy,
    @RequestParam(required = false, value = "reconnect", defaultValue = "false") Boolean reconnect,
    HttpServletRequest request,
    HttpServletResponse response) {

    return ControllerUtils.FORWARD + "/secure/install/authenticate/orgId/" + orgCustomerId
      + "?reconnect=" + reconnect;
  }

  /**
   * Save the settings for a given org customer
   *
   * @param orgCustomerId The orgCustomer Id for whom to load the view
   * @param newConfig     The modelMap of the client
   * @return redirect to get the view with new settings
   */
  @PostMapping("/settings/orgId/{orgCustomerId}")
  public String saveSettings(@PathVariable Long orgCustomerId,
    @ModelAttribute SettingsConfig newConfig) {
    boolean success = false;
    try {
      installService.saveSettings(orgCustomerId, newConfig);
      success = true;
      return "redirect:/secure/install/settings/orgId/" + orgCustomerId + "?saved";
    } catch (NoOrgConfigException e) {
      return "redirect:/secure/install/settings/orgId/" + orgCustomerId + "?error=NoOrgConfig";
    } catch (Exception e) {
      return "redirect:/secure/install/settings/orgId/" + orgCustomerId + "?error=" + e
        .getMessage();
    } finally {
      MDCUtil.endFeature(success ? CompletionCode.SUCCESS : CompletionCode.FAILURE);
      MDC.clear();
    }
  }

  /**
   * Add common items to the "model", including integration definition attributes.
   *
   * @param integrationId Integration ID
   * @param modelMap      Rendering Model
   */
  private void populateModel(final Long integrationId, final ModelMap modelMap) {

    modelMap.addAttribute("integrationId", integrationId);
    modelMap
      .addAttribute("info", subscriptionInfoClient.integrationInfo(integrationId).getResponse());
  }

  /**
   * Persists the users config and performs the install call to int-manager
   *
   * @param orgCustomerId The orgCustomer that is performing the recinstallonnect
   * @param installedBy The name of the user (not username) performing the install
   * @param request The servlet request being made
   * @param response The servlet response to populate
   * @throws IOException
   * @throws ServletException
   */
  private void completeInstallation(
          Long orgCustomerId,
          String installedBy,
          HttpServletRequest request,
          HttpServletResponse response)
          throws IOException, ServletException {
    // Build the config
    OrgConfig orgConfig = new OrgConfig();
    orgConfig.setOrgCustomerId(orgCustomerId);

    // Construct the install call
    SubscriptionPayload subscriptionPayload = new SubscriptionPayload();
    subscriptionPayload.setInstalledBy(installedBy);
    subscriptionPayload.setIntegrationId(integrationId);
    subscriptionPayload.setOrgCustomerId(orgCustomerId);

    String settingsPage = "/int-zipchat-service/secure/install/settings/orgId/" + orgCustomerId;

    try {

      // Register installation with int-manager
      SubscriptionResponse<Long> r =
              subscriptionInfoClient.createOrUpdateSubscription(subscriptionPayload);

      if (r.isSuccess()) {
        // Save the config
        orgConfigRepository.save(orgConfig);

        redirect(orgCustomerId, settingsPage, "success", request, response);

      } else {
        redirect(orgCustomerId, settingsPage, "error=" + r.getErrors().get(0), request, response);
      }

    } catch (Exception e) {
      log.error("Error during installation: {}", e.getMessage(), e);
      redirect(orgCustomerId, settingsPage, "error=" + e.getMessage(), request, response);
    }
  }

  /**
   * Go back to settings page of the integration within Account Console
   *
   * @param orgCustId   The orgCustomerId of the org customer interacting with the config/install
   * @param redirectUrl The settings page URI path
   * @param params      Parameters which will include error messages or a success flag
   * @param request     The servlet request being made
   * @param response    The servlet response to populate
   * @throws IOException
   * @throws ServletException
   */
  private void redirect(Long orgCustId,
    String redirectUrl,
    String params,
    HttpServletRequest request,
    HttpServletResponse response)
    throws IOException, ServletException {
    accountConsoleConfig.redirectBack(request, response, orgCustId, redirectUrl, params);
  }

}
