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
<@emailMacros.msg "email.common.dear" />${emailName},

<#if verificationUrl??>

<@emailMacros.msg "email.service_announcement.verify_account" />

${verificationUrl}

</#if>

<@emailMacros.msg "email.service_announcement.did_you_know" />

<@emailMacros.msg "email.service_announcement.read_our_blog" />
<@emailMacros.msg "email.service_announcement.blog_link" />

<@emailMacros.msg "email.common.kind_regards" />

<@emailMacros.msg "email.common.you_have_received_this_email" />

<#include "email_footer.ftl"/>