<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<!DOCTYPE html>
<html>
	<head>
	<title>${subject}</title>
	</head>
	<body>
		<div style="
					max-width: 736px;
					padding: 32px;
					margin: auto;
					font-family: Arial, helvetica, sans-serif;
					color: #494A4C;
					font-size: 15px;
					line-height: 1.5;
				">
			<img style="width: 120px" src="http://i5.cmail20.com/ti/t/02/55D/4F6/033235/////csimport/orcid-logo_0.jpg" alt="ORCID.org"/>
			<hr style="color: #e0e0e0;background-color: #e0e0e0;border-style: solid;border-width: 2px;" />
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.reactivation.thank_you_message" />
            </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.reactivation.to_reactivate" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
			    <a style="text-decoration: underline;color: #085c77;" href="${reactivationUrl}">${reactivationUrl}</a>
 		    </p>
			<#include "email_footer_html.ftl"/>
		</div>
	 </body>
 </html>
 </#escape>
