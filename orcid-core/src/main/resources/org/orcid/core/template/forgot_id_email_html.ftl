<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<!DOCTYPE html>
<html>
	<head>
	<title>${subject}</title>
	</head>
	<body>
		<div style="padding: 20px; padding-top: 0px; font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
			<img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
		    <hr />
		    <p>
		    	<@emailMacros.msg "email.forgotten_id.the_orcid_associated_email" />
				${submittedEmail}
				<@emailMacros.msg "email.reset_password.is_colon" />
		    <br>
		    	<a href="${baseUri}/${orcid}?lang=${locale}">${baseUri}/${orcid}</a>
		    </p>
			<#include "email_footer_html.ftl"/>
		 </div>
	 </body>
 </html>
 </#escape>
