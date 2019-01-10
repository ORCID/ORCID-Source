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
    			<@emailMacros.msg "email.deactivate.gdpr_you_have_asked.1" /><a href="${baseUri}/${orcid}?lang=${locale}" target="_blank">${baseUri}/${orcid}</a>
    		<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
    			<@emailMacros.msg "email.deactivate.you_have_requested.2" /><a href="${baseUri}${deactivateUrlEndpoint}?lang=${locale}" target="_blank">${baseUri}${deactivateUrlEndpointUrl}</a>
    		</p>
    		<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
	    		<@emailMacros.msg "email.deactivate.gdpr_if_you_do_not" />
		    </p>
    		<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		        <@emailMacros.msg "email.deactivate.please_note.1" /> <a href="https://en.wikipedia.org/wiki/Cryptographic_hash_function" target="_blank"><@emailMacros.msg "email.deactivate.please_note.2" /></a> <@emailMacros.msg "email.deactivate.please_note.3" />
		    </p>
	        <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		        <@emailMacros.msg "email.deactivate.more_info" /><br/><a href="https://support.orcid.org/hc/articles/360006973813" target="_blank">https://support.orcid.org/hc/articles/360006973813</a>
  			</p>
	        <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
                <@emailMacros.msg "email.deactivate.if_you_did" />	<a href=" https://orcid.org/help/contact-us" target="_blank">https://orcid.org/help/contact-us</a> <@emailMacros.msg "email.deactivate.gdpr_or_by_replying" />
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
