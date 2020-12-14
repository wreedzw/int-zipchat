<#include "installDetailsPage.ftl" />

<#assign integrationName="ZipChat" >
<#assign integrationId=22 >
<#assign integrationUrl="/int-zipchat-service" >
<#assign pageTitle="ZipChat Details" >

<@installDetailsPage>

	<@cms name="22/details/description">
	<p>Allow members to chat with each other</p>
	</@>

	<@cms name="22/details/features">
	<h4>Features:</h4>
	<div>
		<ul>
			<li><span>Users can text the keyword JOIN [username] to become a member of the chat group.</span></li>
			<li><span>The can text the keyword LEAVE to have their name removed from the chat group.</span></li>
            <li><span>Messages must be recieved from a group member to be forwarded to all other members.</span></li>
		</ul>
	</div>
	</@>

	<@cms name="22/details/how-it-works">
		<h4>How It Works:</h4>
		<p>ZipChat maintains a list of members and forwards messages to all members.</p>
	</@>

</@installDetailsPage>
