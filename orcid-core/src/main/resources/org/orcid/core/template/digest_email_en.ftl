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
Hi ${emailName},

Here’s what has happened since the last time you visited your ORCID record.

Visit ${baseUri}/notifications?lang=${locale} to view all notifications.

<#compress>
<#if amendedMessageCount gt 0>[${amendedMessageCount}] <#if amendedMessageCount == 1>notification<#else>notifications</#if> from ORCID member organizations that added or updated information on your record</#if>
<#if addActivitiesMessageCount gt 0>[${addActivitiesMessageCount}] <#if addActivitiesMessageCount == 1>Request<#else>Requests</#if> to add or update your ORCID record</#if>
<#if orcidMessageCount gt 0>[${orcidMessageCount}] <#if orcidMessageCount == 1>notification<#else>notifications</#if> from ORCID</#if>
</#compress>


<@emailMacros.msg "email.common.you_have_received_this_email_opt_out.1" />${baseUri}/account?lang=${locale}.

<#include "email_footer.ftl"/>
