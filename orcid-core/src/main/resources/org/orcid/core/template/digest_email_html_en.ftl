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
    <head>
        <title>${subject}</title>
    </head>
    <body>
        <div style="padding: 20px; padding-top: 0px;">
            <img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
            <hr />
            <span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666; font-weight: bold;">
                Hi ${emailName},
            </span>
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                Here’s what has happened since the last time you visited your ORCID record.
            </p>            
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                Visit ${baseUri}/notifications to view all notifications.
            </p>    
            <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                <#compress>
                <#if amendedMessageCount gt 0><div>[${amendedMessageCount}] <#if amendedMessageCount == 1>notification<#else>notifications</#if> from ORCID member organizations that added or updated information on your record</div></#if>
                <#if addActivitiesMessageCount gt 0><div>[${addActivitiesMessageCount}] <#if addActivitiesMessageCount == 1>Request<#else>Requests</#if> to add or update your ORCID record</div></#if>
                <#if orcidMessageCount gt 0><div>[${orcidMessageCount}] <#if orcidMessageCount == 1>notification<#else>notifications</#if> from ORCID</div></#if>
                </#compress>
           </p>
           <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
                <@emailMacros.msg "email.common.you_have_received_this_email_opt_out.1" />${baseUri}/home?lang=${locale}<@emailMacros.msg "email.common.you_have_received_this_email_opt_out.2" />
            </p>                       
            <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
               <#include "email_footer_html.ftl"/>
            </p>
         </div>
     </body>
 </html>
