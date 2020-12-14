<#if (RequestParameters['success']??)>
	<@cms name="${info.id}/alerts/installed" assignTo="alert">
		Congratulations, you are done.
	</@>
	<#assign class='success'>
<#elseif (RequestParameters['error']??)>
	<#assign alert="${RequestParameters['error']}">
<#elseif RequestParameters['saved']??>
	<@cms name="${info.id}/alerts/settingsSaved" assignTo="alert"> Settings successfully updated. </@> 
	<#assign class='success'>
<#elseif (status?? && status.enabled)>
	<@cms name="${info.id}/alerts/syncEnabled" assignTo="alert">
		${info.name} data/message sync was successful.
	</@>
	<#assign class='success'>
<#elseif (status?? && !status.enabled)>
	<@cms name="${info.id}/alerts/syncDisabled" assignTo="alert">
		${info.name} data/message sync was not successful. We will automatically retry in 2 minutes.
	</@>
<#elseif (RequestParameters['error']?? && RequestParameters['error']=='invalidCredentials')>
	<#assign alert='Could not verify the credentials'>
<#elseif (RequestParameters['message']?? && RequestParameters['message']=='uninstalled')>
	<@cms name="${info.id}/alerts/uninstalled" assignTo="alert">
		Successfully uninstalled.
	</@>
    <#assign class='success'>
<#elseif (RequestParameters['remindersSet']??)>
    <@cms name="${info.id}/alerts/remindersSet" assignTo="alert">
        Caleandar reminders settings updated.
    </@>    
    <#assign class='success'>
</#if>