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
			<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C">
				<@emailMacros.msg "email.deactivate.your_orcid_id_will_continue" /> <a href="https://info.orcid.org/privacy-policy/#9_How_long_we_keep_your_data" rel="noopener noreferrer" target="_blank"><@emailMacros.msg "email.deactivate.privacy_policy" /></a> <@emailMacros.msg "email.deactivate.for_information" />
			</p>
			<#include "email_footer_security_html.ftl"/>
		 </div>
	 </body>
 </html>
 </#escape>
