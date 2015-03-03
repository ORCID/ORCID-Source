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
	<title>${subject}</title>
	</head>
	<body>
		<div style="padding: 20px; padding-top: 0px;">
			<img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
		    <hr />
		    <span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				<@emailMacros.msg "email.common.dear" /> ${emailName}<@emailMacros.msg "email.common.dear.comma" />
			</span>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				<@emailMacros.msg "email.welcome.thank_you" />
				<br /><br />
				<a href="${verificationUrl}?lang=${locale}" target="_blank">${verificationUrl}?lang=${locale}</a>
			</p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				<@emailMacros.msg "email.welcome.click_link" />
				<br /><br />
				<@emailMacros.msg "email.welcome.your_id.1" /> ${orcidId}<@emailMacros.msg "email.welcome.your_id.2" /> <a href="${baseUri}/${orcidId}" target="_blank">${baseUri}/${orcidId}</a>
			</p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
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