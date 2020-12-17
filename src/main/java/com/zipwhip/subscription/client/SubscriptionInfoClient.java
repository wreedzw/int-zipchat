package com.zipwhip.subscription.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zipwhip.subscription.domain.IntegrationInfo;
import com.zipwhip.subscription.domain.OrgIntegrationInfo;
import com.zipwhip.subscription.domain.OrgIntegrationInfo.Item;
import com.zipwhip.subscription.domain.SubscriptionInfo;
import com.zipwhip.subscription.domain.SubscriptionPayload;
import com.zipwhip.subscription.domain.SubscriptionResponse;

@FeignClient(name = "int-manager-service", url = "${zipwhip.int-manager.url}")
public interface SubscriptionInfoClient {
    @RequestMapping(
        value = {"/int-manager-service/rest/subscription/{subscriptionId}"},
        method = {RequestMethod.GET}
    )
    SubscriptionResponse<SubscriptionInfo> subscriptions(@PathVariable("subscriptionId") Long subscriptionId);

    @RequestMapping(
        value = {"/int-manager-service/rest/subscription/"},
        method = {RequestMethod.POST}
    )
    SubscriptionResponse<Long> createOrUpdateSubscription(@RequestBody SubscriptionPayload payload);

    @RequestMapping(
        value = {"/int-manager-service/rest/subscription/org/{orgCustomerId}"},
        method = {RequestMethod.GET}
    )
    SubscriptionResponse<OrgIntegrationInfo> getOrgSubscriptionInfo(@PathVariable("orgCustomerId") Long orgCustomerId);

    @RequestMapping(
        value = {"/int-manager-service/rest/subscription/org/{orgCustomerId}/{landline}"},
        method = {RequestMethod.GET}
    )
    SubscriptionResponse<OrgIntegrationInfo> getLandlineSubscriptionInfo(@PathVariable("orgCustomerId") Long orgCustomerId, @PathVariable("landline") String landline);

    @RequestMapping(
        value = {"/int-manager-service/rest/subscription/item/org/{orgCustomerId}/{integrationId}"},
        method = {RequestMethod.GET}
    )
    SubscriptionResponse<Item> getInstalledSubscription(@PathVariable("orgCustomerId") Long orgCustomerId, @PathVariable("integrationId") Long integrationId);

    @RequestMapping(
        value = {"/int-manager-service/rest/subscription/item/org/{orgCustomerId}/{integrationId}/{landline}"},
        method = {RequestMethod.GET}
    )
    SubscriptionResponse<Item> getInstalledLandlineSubscription(@PathVariable("orgCustomerId") Long orgCustomerId, @PathVariable("integrationId") Long integrationId, @PathVariable("landline") String landline);

    @RequestMapping(
        value = {"/int-manager-service/rest/subscription/org/{orgCustomerId}/{integrationId}"},
        method = {RequestMethod.DELETE}
    )
    SubscriptionResponse<Boolean> uninstall(@PathVariable("orgCustomerId") Long orgCustomerId, @PathVariable("integrationId") Long integrationId);

    @RequestMapping(
        value = {"/int-manager-service/rest/subscription/org/{orgCustomerId}/{integrationId}/{landline}"},
        method = {RequestMethod.DELETE}
    )
    SubscriptionResponse<Boolean> uninstallForLandline(@PathVariable("orgCustomerId") Long orgCustomerId, @PathVariable("integrationId") Long integrationId, @PathVariable("landline") String landline);

    @RequestMapping(
        value = {"/int-manager-service/rest/integration/{integrationId}"},
        method = {RequestMethod.GET}
    )
    SubscriptionResponse<IntegrationInfo> integrationInfo(@PathVariable("integrationId") Long integrationId);

    @RequestMapping(
        value = {"/int-manager-service/rest/integration/list"},
        method = {RequestMethod.GET}
    )
    SubscriptionResponse<List<IntegrationInfo>> integrationInfoList();
}
