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
		    	<@emailMacros.msg "email.reset_password.orcid_id" /> ${submittedEmail} <@emailMacros.msg "email.reset_password.is" /> <a href="${baseUri}/${orcid}?lang=${locale}">${baseUri}/${orcid}</a>
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.reset_password.to_reset" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
			    <a id="cy-pwd-reset-url" href="${passwordResetUrl}">${passwordResetUrl}</a>
 		    </p> 		    
 		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">		  
				<@emailMacros.msg "email.reset_password.note" />
		    </p> 		     		    
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">		  
				<@emailMacros.msg "email.reset_password.if_you_did_not" />
		    </p>
			<#include "email_footer_html.ftl"/>
		 </div>
	 </body>
 </html>
 </#escape>
