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
			</p>
    		<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
				<@emailMacros.msg "email.alternate_sign_in.security.1" />
    		</p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <#if (accountWasAdded) ?? && accountWasAdded == true>
                    <@emailMacros.msg "email.alternate_sign_in.security.added" /> ${alternateAccount}
                <#else>
                    <@emailMacros.msg "email.alternate_sign_in.security.removed" /> ${alternateAccount}
                </#if>
            </p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <strong><@emailMacros.msg "email.security.note.shared.1" /></strong><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.2" /><@emailMacros.space />&mdash;<@emailMacros.space /><@emailMacros.msg "email.security.note.shared.3" /><@emailMacros.space /><strong><i><@emailMacros.msg "email.security.note.shared.4" /></i></strong><@emailMacros.space /><@emailMacros.msg "email.security.pwd.note.5" /><@emailMacros.space /><strong><@emailMacros.msg "email.security.note.shared.take_action" /></strong><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.actions.action.1" /><@emailMacros.space /><a href="https://orcid.org/reset-password" rel="noopener noreferrer" target="_blank"><@emailMacros.msg "email.security.note.shared.actions.action.2" /></a><@emailMacros.space /><@emailMacros.msg "email.alternate_sign_in.security.action.3" /><@emailMacros.msg "email.common.period" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.actions.support.1" /><@emailMacros.space /><a href="https://support.orcid.org/hc/en-us/requests/new" rel="noopener noreferrer" target="_blank"><@emailMacros.msg "email.security.note.shared.actions.support.2" /></a><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.actions.support.3" />
            </p>

            <#include "email_footer_security_html.ftl"/>
		 </div>
	 </body>
 </html>
 </#escape>
