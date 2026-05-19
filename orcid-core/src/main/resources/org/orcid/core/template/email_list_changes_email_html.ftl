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
				<@emailMacros.msg "email.changes.we_have_detected_a_change" />
    		</p>
			<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C">
				<#if (emailListChange.addedEmails?has_content)>
					<#list emailListChange.addedEmails as email>
						<@emailMacros.msg "email.changes.the_email_address" /> <a href="mailto:${email.getValue()}">${email.getValue()}</a> <@emailMacros.msg "email.changes.has_been_added" />
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
                <strong><@emailMacros.msg "email.security.note.shared.1" /></strong><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.2" /><@emailMacros.space />&mdash;<@emailMacros.space /><@emailMacros.msg "email.security.note.shared.3" /><@emailMacros.space /><strong></i><@emailMacros.msg "email.security.note.shared.4" /></i></strong><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.5" /><@emailMacros.space /><strong><@emailMacros.msg "email.security.note.shared.take_action" /></strong><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.actions.action.1" /><@emailMacros.space /><a href="https://orcid.org/reset-password" rel="noopener noreferrer" target="_blank"><@emailMacros.msg "email.security.note.shared.actions.action.2" /></a><@emailMacros.msg "email.common.comma" /><@emailMacros.space /><@emailMacros.msg "email.security.note.actions.remove_emails" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.actions.support.1" /><@emailMacros.space /><a href="https://support.orcid.org/hc/en-us/requests/new" rel="noopener noreferrer" target="_blank"><@emailMacros.msg "email.security.note.shared.actions.support.2" /></a><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.actions.support.3" />
			</p>
			<#include "email_footer_security_html.ftl"/>
		 </div>
	 </body>
 </html>
 </#escape>
