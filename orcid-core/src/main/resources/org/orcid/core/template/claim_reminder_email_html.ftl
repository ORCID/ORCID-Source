<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2013 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<#import "email_macros.ftl" as emailMacros />
<!DOCTYPE html>
<html>
	<head>
	<title>${subject}</title>
	</head>
	<body>
		<div style="padding: 20px; padding-top: 0px;">
			<img src="http://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
		    <hr />
		  	<span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666; font-weight: bold;">
		    <@emailMacros.msg "email.common.dear" /> ${emailName}<@emailMacros.msg "email.common.dear.comma" />
		    </span>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                <@emailMacros.msg "email.claim_reminder.this_is_a_reminder.1" />${creatorName}<@emailMacros.msg "email.claim_reminder.this_is_a_reminder.2" />${daysUntilActivation}<@emailMacros.msg "email.claim_reminder.this_is_a_reminder.3" />${creatorName}<@emailMacros.msg "email.claim_reminder.this_is_a_reminder.4" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
		        <@emailMacros.msg "email.claim_reminder.what_do_you" />
 		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">		  
				<@emailMacros.msg "email.claim_reminder.within_the_next.1" />${daysUntilActivation}<@emailMacros.msg "email.claim_reminder.within_the_next.2" />${creatorName}<@emailMacros.msg "email.claim_reminder.within_the_next.3" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
		  		<a href="${verificationUrl}?lang=${locale}">${verificationUrl}</a>
		    </p>		    
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
		        <@emailMacros.msg "email.api_record_creation.what_is_orcid" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
		        <@emailMacros.msg "email.api_record_creation.launched.1" /><a href="${baseUri}/?lang=${locale}">${baseUri}</a><@emailMacros.msg "email.api_record_creation.launched.2" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
		        <@emailMacros.msg "email.api_record_creation.read_privacy.1" /><a href="${baseUri}/privacy-policy/?lang=${locale}">${baseUri}/privacy-policy/</a><@emailMacros.msg "email.api_record_creation.read_privacy.2" />
		        <@emailMacros.msg "email.common.if_you_have_any1" />
		  		<a href="http://support.orcid.org">http://support.orcid.org</a><@emailMacros.msg "email.common.if_you_have_any2" />
		    </p>
		  	<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666; white-space: pre;">
<@emailMacros.msg "email.common.kind_regards" />
<a href="${baseUri}/?lang=${locale}">${baseUri}/?lang=${locale}<a/>
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