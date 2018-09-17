<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<!DOCTYPE html>
<html>
	<head>
		<title>${subject}</title>
	</head>
	<body>
		<div style="padding: 20px; padding-top: 0px;">
			<img src="http://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
		    <hr />
		  	<span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C; font-weight: bold;">
			    <@emailMacros.msg "email.common.dear" /><@emailMacros.space />${emailNameForDelegate}<@emailMacros.msg "email.common.dear.comma" />
		    </span>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.added_as_delegate.you_have.1" /><@emailMacros.space />${grantingOrcidName}<@emailMacros.space /><@emailMacros.msg "email.added_as_delegate.you_have.2" /><a href="${baseUri}/${grantingOrcidValue}?lang=${locale}" target="orcid.blank">${baseUri}/${grantingOrcidValue}</a><@emailMacros.msg "email.added_as_delegate.you_have.3" />${grantingOrcidName}<@emailMacros.msg "email.added_as_delegate.you_have.4" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.added_as_delegate.for_a_tutorial" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		    	<@emailMacros.msg "email.added_as_delegate.if_you_have.1" />${grantingOrcidName}<@emailMacros.space /><@emailMacros.msg "email.added_as_delegate.if_you_have.2" /><@emailMacros.space />${grantingOrcidEmail}<@emailMacros.msg "email.added_as_delegate.if_you_have.3" />
		    </p>		    
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
				<@emailMacros.msg "email.common.you_have_received_this_email" />
			</p>
			<hr />
			<p>
			   <#include "email_footer_html.ftl"/>
			</p>
		 </div>
	 </body>
 </html>
 </#escape>
