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
                <@emailMacros.msg "email.amend.thought_you.1" /> ${amenderName} <@emailMacros.msg "email.amend.thought_you.2" />
		    </p>
		    
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                <@emailMacros.msg "email.amend.please_click" />
                ${baseUri}/my-orcid?lang=${locale}
		    </p>
		    
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
		    	<@emailMacros.msg "email.common.kind_regards" />
				${baseUri}/?lang=${locale}
		    </p>

			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
				<@emailMacros.msg "email.common.you_have_received_this_email_opt_out.1" />${baseUri}/?lang=${locale}<@emailMacros.msg "email.common.you_have_received_this_email_opt_out.2" />
			</p>
					    
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
			   <#include "email_footer_html.ftl"/>
			</p>
		 </div>
	 </body>
 </html>