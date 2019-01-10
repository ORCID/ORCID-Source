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
		  	<span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C; font-weight: bold;">
		    	<@emailMacros.msg "email.common.dear" /><@emailMacros.space />${emailName}<@emailMacros.msg "email.common.dear.comma" />
		    </span>		    	
    		<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
    			<@emailMacros.msg "email.deactivate.gdpr_you_have_asked.1" /><a href="${baseUri}/${orcid}?lang=${locale}">${baseUri}/${orcid}</a>
    		<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
    			<@emailMacros.msg "email.deactivate.you_have_requested.2" /><a href="${baseUri}${deactivateUrlEndpoint}?lang=${locale}">${baseUri}${deactivateUrlEndpointUrl}</a>
    		</p>
    		<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
	    		<@emailMacros.msg "email.deactivate.gdpr_if_you_do_not" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		    		<@emailMacros.msg "email.deactivate.gdpr_please_note" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		    		<@emailMacros.msg "email.deactivate.gdpr_once_you_have" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		    		<@emailMacros.msg "email.deactivate.gdpr_if_you_would" /> <a target="orcid.contact" href="${baseUri}/help/contact-us"><@emailMacros.msg "email.deactivate.gdpr_orcid_support" /></a> <@emailMacros.msg "email.deactivate.gdpr_to_request_removal" /> <a href="${baseUri}/${orcid}?lang=${locale}">${baseUri}/${orcid}</a> <@emailMacros.msg "email.deactivate.gdpr_at_any_point" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
	  			<@emailMacros.msg "email.deactivate.if_you_did" /> <a target="orcid.blank" href="${baseUri}/help/contact-us">${baseUri}/help/contact-us</a> <@emailMacros.msg "email.deactivate.gdpr_or_by_replying" />
	    	</p>			    	
		  	<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C; white-space: pre;">
<@emailMacros.msg "email.common.warm_regards" />
<a href='<@emailMacros.msg "email.common.need_help.description.2.href" />' target="orcid.contact_us"><@emailMacros.msg "email.common.need_help.description.2.href" /></a>
			</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
				<a href="${baseUri}/home?lang=${locale}">${baseUri}/<a/>
			</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
			   <#include "email_footer_html.ftl"/>
			</p>
		 </div>
	 </body>
 </html>
 </#escape>
