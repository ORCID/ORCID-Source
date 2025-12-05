<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<!DOCTYPE html>
<html>
	<head>
	<title>${subject}</title>
	</head>
	<body>
		<div style="padding: 20px; padding-top: 0px;">
			<img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
		    <hr />
    		<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
    			<@emailMacros.msg "email.welcome.your_id.id" /> ${orcid}
				<br>
				<@emailMacros.msg "email.welcome.your_id.link" />:<@emailMacros.space /><a href="${baseUri}/${orcid}" target="orcid.blank">${baseUri}/${orcid}</a>
			</p>
    		<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
				<@emailMacros.msg "email.changes.we_have_detected" />
    		</p>
			<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C">
				<#if (emailListChange.verifiedEmails?has_content)>
					<#list emailListChange.verifiedEmails as email>
						<@emailMacros.msg "email.changes.the_email_address" /> <a href="mailto:${email}">${email}</a> <@emailMacros.msg "email.changes.has_been_verified" />
						<br>
					</#list>
				</#if>
				<#if (emailListChange.addedEmails?has_content)>
					<#list emailListChange.addedEmails as email>
						<@emailMacros.msg "email.changes.the_email_address" /> <a href="mailto:${email.getEmail()}">${email.getEmail()}</a> <@emailMacros.msg "email.changes.has_been_added" />
						<br>
					</#list>
				</#if>
				<#if (emailListChange.removedEmails?has_content)>
					<#list emailListChange.removedEmails as email>
						<@emailMacros.msg "email.changes.the_email_address" /> <a href="mailto:${email.getEmail()}">${email.getEmail()}</a> <@emailMacros.msg "email.changes.has_been_removed" />
						<br>
					</#list>
				</#if>
			</p>
			<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
				<@emailMacros.msg "email.security.if_you_did_not_make_these_changes" /> <a href="https://orcid.org/reset-password" rel="noopener noreferrer" target="_blank"><@emailMacros.msg "email.security.update_your_password" /></a> <@emailMacros.msg "email.changes.and_remove" />
			</p>
			<#include "email_footer_security_html.ftl"/>
		 </div>
	 </body>
 </html>
 </#escape>
