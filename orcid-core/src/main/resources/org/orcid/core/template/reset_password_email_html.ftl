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
		  	<span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666; font-weight: bold;">
		      <@emailMacros.msg "email.common.dear" /><@emailMacros.space />${emailName}<@emailMacros.msg "email.common.dear.comma" />
		    </span>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
		    	<@emailMacros.msg "email.reset_password.sorry" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
		    	<@emailMacros.msg "email.reset_password.orcid_id" /> ${submittedEmail} <@emailMacros.msg "email.reset_password.is" /> <a href="${baseUri}/${orcid}?lang=${locale}">${baseUri}/${orcid}</a>
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                <@emailMacros.msg "email.reset_password.to_reset" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
			    <a href="${passwordResetUrl}">${passwordResetUrl}</a>
 		    </p> 		    
 		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">		  
				<@emailMacros.msg "email.reset_password.note" />
		    </p> 		     		    
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">		  
				<@emailMacros.msg "email.reset_password.if_you_did_not" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
		  		<@emailMacros.msg "email.common.if_you_have_any1" /> <a href="<@emailMacros.msg "email.common.need_help.description.2.href" />"><@emailMacros.msg "email.common.need_help.description.2.href" /></a><@emailMacros.msg "email.common.if_you_have_any2" />
		    </p>		    
		  	<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666; white-space: pre;">
<@emailMacros.msg "email.common.warm_regards" />
<a href='<@emailMacros.msg "email.common.need_help.description.2.href" />' target="orcid.contact_us"><@emailMacros.msg "email.common.need_help.description.2.href" /></a>
			</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
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
