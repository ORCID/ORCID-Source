<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<#import "email_macros.ftl" as emailMacros />
<!DOCTYPE html>
<html>
	<head>
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">
	<style>
		.content {
			padding: 20px; 
			padding-top: 10px; 
			width: 700px;
			margin: auto;
		}
		.btn-primary {
			background: #31789B;			
		}

		.btn-primary:hover {
			background: #a6ce39;
			text-shadow: none;
			border-color: #A6CE39; 
		}
	</style>
	<title>${subject}</title>
	</head>
	<body>
		<div class="content">
			<img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
		    <hr />
		    <span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				<@emailMacros.msg "email.common.dear" /> ${emailName}<@emailMacros.msg "email.common.dear.comma" />
			</span>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				<@emailMacros.msg "email.welcome.thank_you.1" />${source_name_if_exists} <@emailMacros.msg "email.welcome.thank_you.2" />
				<br />
				<div align="center">
					<a href="${verificationUrl}?lang=${locale}" target="_blank" class="btn btn-primary"><@emailMacros.msg "email.button" /></a>
				</div>
				<br />
			</p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				<@emailMacros.msg "email.welcome.click_link" />
				<br />
				<a href="${verificationUrl}?lang=${locale}" target="_blank">${verificationUrl}?lang=${locale}</a>
				<br />
				<ul>
					<li><@emailMacros.msg "email.welcome.your_id.id" /> ${orcidId}</li>
					<li><@emailMacros.msg "email.welcome.your_id.link" /> <a href="${baseUri}/${orcidId}" target="_blank">${baseUri}/${orcidId}</a></li>
				</ul>				
			</p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 18px; color: #666666; font-weight: bold;">
				<@emailMacros.msg "email.welcome.next_steps" />
			</p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				<strong><@emailMacros.msg "email.welcome.next_steps.1" /></strong>
			</p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				<@emailMacros.msg "email.welcome.next_steps.1.description.1.1" /> ${baseUri}/my-orcid <@emailMacros.msg "email.welcome.next_steps.1.description.1.2" />
				<@emailMacros.msg "email.welcome.next_steps.1.description.2" />
				<br /><br />
				<@emailMacros.msg "email.welcome.next_steps.1.description.tips" />				
			</p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				<strong><@emailMacros.msg "email.welcome.next_steps.2" /></strong>
			</p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				<@emailMacros.msg "email.welcome.next_steps.2.description" />
			</p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				<strong><@emailMacros.msg "email.welcome.need_help" /></strong>
			</p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				<@emailMacros.msg "email.welcome.need_help.description" />
			</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666; white-space: pre;">
<@emailMacros.msg "email.common.kind_regards" />
<a href="${baseUri}/home?lang=${locale}">${baseUri}/</a>
			</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
				<@emailMacros.msg "email.common.you_have_received_this_email" />
			</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
			   <#include "email_footer_html.ftl"/>
			</p>
		 </div>
	 </body>
 </html>