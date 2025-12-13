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
    			<@emailMacros.msg "email.deprecate.security.your_orcid_record" /><@emailMacros.space />${deprecated_orcid}<@emailMacros.space /><@emailMacros.msg "email.deprecate.security.has_been_deprecated" /><@emailMacros.space />${orcid}.
			</p>
    		<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
				<@emailMacros.msg "email.deprecate.security.all_information_deleted" /><@emailMacros.space /><a href="${baseUri}/${orcid}" target="orcid.blank">${baseUri}/${orcid}</a>
    		</p>
			<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
				<#list emailList as email>
       				<a href="mailto:${email.getEmail()}">${email.getEmail()}</a>
 				</#list>
			</p>
			<#include "email_footer_security_html.ftl"/>
		 </div>
	 </body>
 </html>
 </#escape>
