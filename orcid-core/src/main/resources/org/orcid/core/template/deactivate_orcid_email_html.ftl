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
    			<@emailMacros.msg "email.deactivate.you_have_asked" /> <a href="${baseUri}/${orcid}?lang=${locale}" target="_blank">(${baseUri}/${orcid})</a>
    		<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
    			<@emailMacros.msg "email.deactivate.please_click_the_link" /> <a href="${baseUri}${deactivateUrlEndpoint}?lang=${locale}" target="_blank">${baseUri}${deactivateUrlEndpointUrl}</a>
    		</p>
			<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C; font-style: italic">
				<@emailMacros.msg "email.deactivate.please_note" />
			</p>
    		<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
	    		<@emailMacros.msg "email.deactivate.if_you_no_longer_want" />
		    </p>
    		<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		        <@emailMacros.msg "email.deactivate.after_your_account_is_deactivated" />
		    </p>
    		<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		        <@emailMacros.msg "email.deactivate.you_can_reactivate" />
		    </p>
			<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
				<@emailMacros.msg "email.deactivate.for_more_information" /> <a href="https://support.orcid.org/hc/articles/360006973813" rel="noopener noreferrer" target="_blank">https://support.orcid.org/hc/articles/360006973813</a>
			</p>
			<p
			<#include "email_footer_html.ftl"/>
		 </div>
	 </body>
 </html>
 </#escape>
