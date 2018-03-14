<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<!DOCTYPE html>
<html>
	<head>
	<title>${subject}</title>
	</head>
	<body>
		<div  style="padding: 20px; padding-top: 10px; margin: auto;">
			<img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
		    <hr />
		  	<span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666; font-weight: bold;">
		      <@emailMacros.msg "email.common.dear" /><@emailMacros.space />${emailName}<@emailMacros.msg "email.common.dear.comma" />
		    </span>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
		        <#if isReminder?? && isReminder>
                    <#if isPrimary?? && isPrimary>
                        <@emailMacros.msg "email.verify.primary_reminder_v2" /><@emailMacros.space />
                    </#if>
                    <@emailMacros.msg "email.verify.click_link" />
                <#else>
                    <@emailMacros.msg "email.verify.thank_you" />
                </#if>
                <br /><br />
                <a href="${verificationUrl}?lang=${locale}" target="orcid.blank">${verificationUrl}</a>        
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
		    	<@emailMacros.msg "email.verify.1" /><@emailMacros.space />${orcid}<@emailMacros.msg "email.verify.2" /><@emailMacros.space /><a href="${baseUri}/${orcid}?lang=${locale}" target="orcid.blank">${baseUri}/${orcid}</a><@emailMacros.space /><@emailMacros.msg "email.verify.primary_email_1" /><@emailMacros.space />${primaryEmail}<@emailMacros.msg "email.verify.primary_email_2" />. 	        		    	
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
		        <@emailMacros.msg "email.verify.if_you_did_not" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">		  
				<@emailMacros.msg "email.common.did_you_know" /><@emailMacros.space /><a href="${baseUri}/blog">${baseUri}/blog</a>
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
		        <@emailMacros.msg "email.common.need_help.description.1" /><@emailMacros.space /><a href='<@emailMacros.msg "email.common.need_help.description.1.href" />'><@emailMacros.msg "email.common.need_help.description.1.text" /></a><@emailMacros.space /><@emailMacros.msg "email.common.need_help.description.2" /><@emailMacros.space /><a href='<@emailMacros.msg "email.common.need_help.description.2.href" />'><@emailMacros.msg "email.common.need_help.description.2.text" /></a>
		    </p>		    
		  	<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666; white-space: pre;">
<@emailMacros.msg "email.common.kind_regards.simple" />
<a href="${baseUri}/home?lang=${locale}">${baseUri}/</a>
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
