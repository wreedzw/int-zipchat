<#include "framedPage.ftl" />
<#include "alerts.ftl" />
<#setting number_format="0.##">
<#assign pageTitle="${info.name} - Verify Credentials">

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

		.form label {
			font-size: 14px;
			color: #333333;
			margin-bottom: 10px;
		}

		.form input, .form select {
			width: 280px;
			height: 35px;
			border: 1px solid #ccc;
			border-radius: 1px;
			padding: 0 10px;
			color: #333333;
			margin-bottom: 20px;
		}

	</style>

	<ul class="breadcrumb">
		<li><a href="/secure/auth/frame">Integrations</a></li>
		<li><a href="${info.url}/secure/install/settings/orgId/${orgCustomerId}">${info.name}</a></li>
		<li>Verify Credentials</li>
	</ul>

	<div class="int-container">

		<#if alert??>
			<div class="int-alert ${class!''}">
				<div class="line"></div>
				<div class="message">${alert}</div>
			</div>

			<script type="text/javascript">
				gaEvent('${info.name}', 'Authentication', '${alert}', ${class???then(1,0)});
			</script>

			<br />
		</#if>

		<div>
			<form id="installForm" class="form" action="${info.url}/secure/install/connect/${orgCustomerId}" method="post" target="_parent">
				<input id="installedBy" type="hidden" name="installedBy">
				<input type="hidden" name="platform" value="${orgCustomerId}">
				<input name="reconnect" type="hidden" value="${reconnect?c}">

				<div style="display: inline-grid; font-size: 14px;">
					<label>ZipChat Domain</label><input required="true" data-testid="Domain" name="gwBaseUrl" placeholder="i.e. http://zipwhip.cbt-dev.net/cc/ws/">
					<label>Username</label><input data-testid="UsernameInput" required="true" name="username">
					<label>Password</label><input data-testid="PasswordInput" required="true" type="password" name="password">

				</div>

				<div class="block">
					<button data-testid="VerifyButton" class="right"  style="margin-left: 10px;" onmousedown="gaEvent('${info.name}', 'click', 'Verify')">Verify</button>
					<a href="${info.url}/secure/install/details/orgId/${orgCustomerId}"><button data-testid="CancelButton" class="cancel right" type="button">Cancel</button></a>
				</div>

			</form>

			<script type="text/javascript">
				$('#installedBy').val(window.parent.getZwUserName());
			</script>
		</div>

	</div>

</@framedPage>
