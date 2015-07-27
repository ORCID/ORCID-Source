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
<!DOCTYPE html>
<html>
    <head></head>
    <body>
        <div style="padding: 20px; padding-top: 0px; font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666; width: 800px;">
            <img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
            <hr />
            <span style="font-weight: bold;">
                Hi ${emailName},
            </span>
            <p>
                You have <${orcidMessageCount}> new <#if orcidMessageCount == 1>notification<#else>notifications</#if> in your ORCID inbox - see summary below. Please visit your ORCID <a href="${baseUri}/notifications?lang=${locale}" style="color: #338caf;">account inbox</a> to take action.
            </p>   
            <#if addActivitiesMessageCount gt 0>    
	            <p>
	                Requests to add to or update items in your ORCID record [${addActivitiesMessageCount}] (Please action as soon as possible)
	            </p>            
	            <p>  
	                <ul>
	                	<#-- Here goes the info -->
	                    <li></li>                    
	                </ul>
	            </p>
	        </#if>
            <#if amendedMessageCount gt 0>
	            <p>
	                Updates to your ORCID record [${amendedMessageCount}]
	                <ul>
	                	<#-- Here goes the info -->
	                    <li></li>                    
	                </ul>            
	            </p>
	        </#if>
            <p>
                <a href="" style="text-decoration: none; text-align: center;">
                    <span style="padding-top: 10px; padding-bottom: 10px; padding-left: 15px; padding-right: 15px; background: #338caf; color: #FFF; display: block; width: 300px;">
                        View in your ORCID inbox
                    </span>
                </a>
            </p>
            <p>
                You have received this message because you opted in to receive Inbox notifications about your ORCID record. <a href="${baseUri}/notifications?lang=${locale}" style="color: #338caf;">Learn more about how the Inbox works.</a>
            </p>
            <p>
                You may adjust your email frequency and subscription preferences in your account settings.
            </p>
            <p>
               <#include "email_footer_html.ftl"/>
            </p>            
        </div>      
    </body>
</html>