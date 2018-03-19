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
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                <@emailMacros.msg "email.reactivation.thank_you" />
            </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                <@emailMacros.msg "email.reactivation.to_reactivate" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
			    <a href="${reactivationUrl}">${reactivationUrl}</a>
 		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">		  
				<@emailMacros.msg "email.reactivation.after" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
		  		<@emailMacros.msg "email.common.if_you_have_any1" /><a href="<@emailMacros.knowledgeBaseUri />"><@emailMacros.knowledgeBaseUri /></a><@emailMacros.msg "email.common.if_you_have_any2" />
		    </p>		    
		  	<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666; white-space: pre;">
<@emailMacros.msg "email.common.kind_regards" />
<a href="${baseUri}/home?lang=${locale}">${baseUri}/<a/>
			</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
				<@emailMacros.msg "email.common.you_have_received_this_email" />
			</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
			   <#include "email_footer_html.ftl"/>
			</p>
		 </div>
	 </body>
 </html>
 </#escape>
