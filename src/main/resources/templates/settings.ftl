<#include "framedPage.ftl" />
<#setting number_format="0.##">

<#if params.status.installed>
	<#assign pageTitle="ZipChat Settings - Installed">
<#else>
	<#assign pageTitle="ZipChat Settings - Available">
</#if>

<#if (RequestParameters['success']??)>
	<#assign alert='Congratulations, you are done.' class='success'>
<#elseif (params.status.syncActive?? && params.status.syncActive)>
	<#assign alert='ZipChat features are successful.' class='success'>
<#elseif (params.status.syncActive?? && !params.status.syncActive)>
	<#assign alert='ZipChat features unavailable. Please retry in 2 minutes.'>
</#if>

<#if (RequestParameters['error']?? && RequestParameters['error'] == 'noOrgConfig')>
	<@cms name="22/alerts/noOrgConfig" assignTo="alert">
	Not found orgConfig.
	</@>
<#elseif (RequestParameters['error']??)>
	<#assign alert="${RequestParameters['error']}">
</#if>

<@framedPage>

<style>
.setting-tab {
	font-size: 14px;
	color: #333333;
	padding: 10px 20px;
	cursor: pointer;
}

.setting-tab.active {
	border-bottom: 2px solid red;
	font-weight: bold;
}
</style>

<ul class="breadcrumb">
	<li><a href="/secure/auth/frame" data-testid="breadcrumb">Integrations</a></li>
	<li>ZipChat Settings</li>
</ul>

<div class="int-container">

	<#if alert??>
		<div class="int-alert ${class!''}">
			<div class="line"></div>
			<div class="message">${alert}</div>
		</div>

		<script type="text/javascript">
			gaEvent('ZipChat', 'Authentication', '${alert}', ${class???then(1,0)});
		</script>


		<br/>
	</#if>

	<div class="flex int-title">
		<img class="int-image" src="${cdn}/images/21_small.png" />
		<@cms name="22/settings/header">
			<span>ZipChat</span>
		</@cms>
	</div>

	<@cms name="22/settings/description">
	<p>This integration allows members to chat with each other.</p>
	</@>

	<hr />

	<@cms name="22/settings/authentication/title">
	<h3>Authentication</h3>
	</@>

	<div class="block">

		<#if params.status.installed>

			<div class="left">
				<@cms name="22/settings/authentication/description">
				<p>This integration forwards a message from one member to all other members.</p>
				</@>
				<div style="font-size: 14px; margin-bottom: 9px; color: #aaaaaa; line-height: 20px; clear: left">Added by <a id="installBy" href="javascript:void(0)">${params.status.installedBy}</a> on ${params.status.installedOn.format("MMMM dd'th,' yyyy")}</div>

			</div>

			<#if params.status.installed>

				<#assign integrationName="ZipChat">
				<#assign reconnectFormTarget="_self">
				<#assign removeFormTarget="_parent">
				<#assign removeAuthUrl="/int-zipchat-service/secure/uninstall/orgId/${orgCustomerId}">

			<form id="removeAuthForm" method="post" action="${removeAuthUrl}" target="${removeFormTarget}">
				<button id="removeAuthButton" type="button" class="right uninstall" onclick="gaEvent('${integrationName}', 'click', 'Uninstall');">Uninstall</button>
			</form>

			<script type="text/javascript">

				$('#removeAuthButton').click(function () {
					if (document.domain.indexOf('zipwhip.com') >= 0) {
						// If this isn't localhost, we can set the domain to zipwhip to bypass the iframe's same origin policy
						document.domain = 'zipwhip.com';
					}
					window.parent.showConfirmationPopup(
							'Remove ${integrationName} Integration',
							'Are you sure you want to remove the ${integrationName} integration? Removing this integration will revoke all permissions and authorizations for this integration.',
							function () { $('#removeAuthForm').submit(); }
					);
				});
			</script>
		</#if>

		</#if>

	</div>

	<#include "zipchatSettings.ftl"/>

</div>

</@framedPage>
