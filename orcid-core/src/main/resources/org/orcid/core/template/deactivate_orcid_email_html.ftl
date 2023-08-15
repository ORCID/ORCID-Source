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
    			<@emailMacros.msg "email.deactivate.gdpr_you_have_asked.1" /><a href="${baseUri}/${orcid}?lang=${locale}" target="_blank">${baseUri}/${orcid}</a>
    		<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
    			<@emailMacros.msg "email.deactivate.you_have_requested.2" /><a href="${baseUri}${deactivateUrlEndpoint}?lang=${locale}" target="_blank">${baseUri}${deactivateUrlEndpointUrl}</a>
    		</p>
    		<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
	    		<@emailMacros.msg "email.deactivate.gdpr_if_you_do_not" />
		    </p>
    		<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		        <@emailMacros.msg "email.deactivate.please_note.1" /> <a href="https://en.wikipedia.org/wiki/Cryptographic_hash_function" rel="noopener noreferrer" target="_blank"><@emailMacros.msg "email.deactivate.please_note.2" /></a> <@emailMacros.msg "email.deactivate.please_note.3" />
		    </p>
    		<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		        <@emailMacros.msg "email.deactivate.reactivate_anytime" /> <a href="https://support.orcid.org/hc/articles/360006973813" rel="noopener noreferrer" target="_blank">https://support.orcid.org/hc/articles/360006973813</a>
		    </p>
			<#include "email_footer_html.ftl"/>
		 </div>
	 </body>
 </html>
 </#escape>
