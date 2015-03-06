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
		<div style="padding: 20px; padding-top: 10px; width: 700px; margin: auto;">
			<img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
		    <hr />
		    <span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				<@emailMacros.msg "email.common.dear" /> ${emailName}<@emailMacros.msg "email.common.dear.comma" />
			</span>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				<@emailMacros.msg "email.welcome.thank_you.1" />${source_name_if_exists} <@emailMacros.msg "email.welcome.thank_you.2" />
				<br />
				<div align="center">
					<a href="${verificationUrl}?lang=${locale}" target="_blank" 
					style="text-decoration: none; background: #31789B; border-color: #357ebd; color: #fff; margin-bottom: 0; font-weight: 400; text-align: center; vertical-align: middle; cursor: pointer; border: 1px solid transparent; white-space: nowrap; padding: 6px 12px; font-size: 14px; line-height: 1.428571429; border-radius: 4px;"><@emailMacros.msg "email.button" /></a>
				</div>
				<br />
			</p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				<@emailMacros.msg "email.welcome.click_link" />
				<br />
				<a href="${verificationUrl}?lang=${locale}" target="_blank">${verificationUrl}?lang=${locale}</a>
				<br />
				<ul>
					<li style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;"><@emailMacros.msg "email.welcome.your_id.id" /> <a href="${baseUriHttp}/${orcidId}" target="_blank">${orcidId}</a></li>
					<li style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;"><@emailMacros.msg "email.welcome.your_id.link" /> <a href="${baseUriHttp}/${orcidId}" target="_blank">${baseUriHttp}/${orcidId}</a></li>
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