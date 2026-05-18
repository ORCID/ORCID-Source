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
    			<@emailMacros.msg "email.deactivate.your_orcid_account_has_been_deactivated" />
    		</p>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.deactivate.your_orcid_id_will_continue" /><@emailMacros.space /><a href="https://info.orcid.org/privacy-policy/#9_How_long_we_keep_your_data" rel="noopener noreferrer" target="_blank"><@emailMacros.msg "email.deactivate.privacy_policy" /></a><@emailMacros.space /><@emailMacros.msg "email.deactivate.for_information" />
            </p>
			<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C">
                <strong><@emailMacros.msg "email.security.note.shared.1" /></strong><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.2" /><@emailMacros.space />&mdash;<@emailMacros.space /><@emailMacros.msg "email.security.note.shared.3" /><@emailMacros.space /><strong></i><@emailMacros.msg "email.security.note.shared.4" /></i></strong><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.5" /><@emailMacros.space /><@emailMacros.msg "email.security.deactivate.note.actions.1" /><@emailMacros.space /><strong><@emailMacros.msg "email.security.deactivate.note.actions.2" /></strong><@emailMacros.space /><@emailMacros.msg "email.security.deactivate.note.actions.3" /><@emailMacros.space /><a href="https://support.orcid.org/hc/en-us/requests/new" rel="noopener noreferrer" target="_blank"><@emailMacros.msg "email.security.note.shared.contact_support" /></a>
			</p>
			<#include "email_footer_security_html.ftl"/>
		 </div>
	 </body>
 </html>
 </#escape>
