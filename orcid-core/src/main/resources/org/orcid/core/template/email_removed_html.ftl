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
		    	<@emailMacros.msg "email.email_removed.the_primary" /> 
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		    	<@emailMacros.msg "email.email_removed.while.1" /><@emailMacros.space /><a href="${baseUri}/${orcid}">${baseUri}/${orcid}</a><@emailMacros.msg "email.email_removed.while.2" />
		    </p>
			<#include "email_footer_html.ftl"/>
		 </div>
	 </body>
 </html>
 </#escape>
