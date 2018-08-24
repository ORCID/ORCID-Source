<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<!DOCTYPE html>
<html>
	<head>
	<title>
        <@emailMacros.msg "email.admin_delegate_request.title.1" /><@emailMacros.space />${trustedOrcidName}<@emailMacros.space /><@emailMacros.msg "email.admin_delegate_request.title.2" />
    </title>
	</head>
	<body>
		<div style="padding: 20px; padding-top: 0px;">
			<img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
		    <hr />
		  	<span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C; font-weight: bold;">
			   <@emailMacros.msg "email.common.dear" /><@emailMacros.space />${emailNameForDelegate}<@emailMacros.msg "email.common.dear.comma" />
		    </span>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
				<@emailMacros.msg "email.admin_delegate_request.you_have.1" /><@emailMacros.space />${trustedOrcidName}
                <@emailMacros.space /><@emailMacros.msg "email.admin_delegate_request.you_have.2" />${baseUri}/${trustedOrcidValue}<@emailMacros.msg "email.admin_delegate_request.you_have.3" />
                ${trustedOrcidValue}<@emailMacros.msg "email.admin_delegate_request.you_have.4" />${baseUri}/${managedOrcidValue}<@emailMacros.msg "email.admin_delegate_request.you_have.5" />${managedOrcidValue}<@emailMacros.msg "email.admin_delegate_request.you_have.6" />
		    </p>		   
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		    	<@emailMacros.space /><@emailMacros.msg "email.admin_delegate_request.you_have.7" />
			</p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		    	<@emailMacros.msg "email.admin_delegate_request.you_have.8" /><br /><a href="${link}" target="orcid.blank">${link}</a>
		    </p>		     
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.admin_delegate_request.for_a_tutorial" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C; white-space: pre;">
<@emailMacros.msg "email.admin_delegate_request.kind_regards" />
<a href="${baseUri}/home?lang=${locale}">${baseUri}/</a>		
		    </p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
				<@emailMacros.msg "email.common.you_have_received_this_email" />
			</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
			   <#include "email_footer_html.ftl"/>
			</p>
		 </div>
	 </body>
 </html>
 </#escape>
