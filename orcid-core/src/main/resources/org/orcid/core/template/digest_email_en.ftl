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
You have <${orcidMessageCount}> new <#if orcidMessageCount == 1>notification<#else>notifications</#if> in your ORCID inbox - see summary below. Please visit your ORCID account inbox to take action. [1]

<#compress>
<#if addActivitiesMessageCount gt 0>
<#if addActivitiesMessageCount == 1>Request<#else>Requests</#if> to add to or update items in your ORCID record (${addActivitiesMessageCount} <#if addActivitiesMessageCount == 1>request<#else>requests</#if>) (Please action as soon as possible)
<#-- Here goes the info -->
- 
</#if>

<#if amendedMessageCount gt 0>
<#if amendedMessageCount == 1>Update<#else>Updates</#if> to your ORCID record (${amendedMessageCount} <#if amendedMessageCount == 1>update<#else>updates</#if>)
<#-- Here goes the info -->
- 
</#if>
</#compress>

VIEW YOUR ORCID INBOX: https://qa.orcid.org/inbox

You have received this message because you opted in to receive Inbox notifications about your ORCID record. Learn more about how the Inbox works [2].
You may adjust your email frequency and subscription preferences in your account settings [3].

[1] ORCID account inbox: https://qa.orcid.org/inbox
[2] About the ORCID Inbox: http://support.orcid.org/knowledgebase/articles/665437
[3] ORCID account settings: https://qa.orcid.org/account 
[4] Email preferences: https://qa.orcid.org/account 
[5] ORCID privacy policy: http://qa.orcid.org/footer/privacy-policy

<#include "email_footer.ftl"/>