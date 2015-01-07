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

Visit ${baseUri}/notifications to view all notifications.

<#-- XXX Implement this [#] notifications from ORCID member organizations that added or updated information on your record -->
<#if addActivitiesMessageCount gt 0>[${addActivitiesMessageCount}] Requests to add or update your ORCID record</#if>
<#if orcidMessageCount gt 0>[${orcidMessageCount}] notifications from ORCID</#if>

<@emailMacros.msg "email.common.you_have_received_this_email_opt_out.1" />${baseUri}/home?lang=${locale}<@emailMacros.msg "email.common.you_have_received_this_email_opt_out.2" />
<#include "email_footer.ftl"/>
