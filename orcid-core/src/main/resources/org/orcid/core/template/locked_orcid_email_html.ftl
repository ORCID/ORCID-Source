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
		    	<#if features["HTTPS_IDS"]?? && features["HTTPS_IDS"]> 
		    		<@emailMacros.msg "email.locked.this_is_an_important_message.1" /><a href="${baseUri}/${orcid}?lang=${locale}">${baseUri}/${orcid}</a><@emailMacros.msg "email.locked.this_is_an_important_message.2" />
		    	<#else>
		    		<@emailMacros.msg "email.locked.this_is_an_important_message.1" /><a href="${baseUriHttp}/${orcid}?lang=${locale}">${baseUriHttp}/${orcid}</a><@emailMacros.msg "email.locked.this_is_an_important_message.2" />
		    	</#if>
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		    	<@emailMacros.msg "email.locked.orcid_registry_provides_identifiers" /><@emailMacros.space /><@emailMacros.msg "email.locked.please_see" /><@emailMacros.space /><a href="https://info.orcid.org/terms-of-use/"><@emailMacros.msg "email.locked.terms_of_use" /></a><@emailMacros.space /><@emailMacros.msg "email.locked.further_information" />
		    </p>
			<#include "email_footer_html.ftl"/>
		 </div>
	 </body>
 </html>
 </#escape>
