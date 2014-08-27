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
<@emailMacros.msg "email.common.dear" /> ${emailName}<@emailMacros.msg "email.common.dear.comma" />

<@emailMacros.msg "email.verify.thank_you" /> ${verificationUrl}?lang=${locale}

<@emailMacros.msg "email.verify.1" />${orcid}<@emailMacros.msg "email.verify.2" />
${baseUri}/${orcid}?lang=${locale} <@emailMacros.msg "email.verify.primary_email_1" /> ${primaryEmail}<@emailMacros.msg "email.verify.primary_email_2" />.
 
<@emailMacros.msg "email.verify.if_you_did_not" />

<@emailMacros.msg "email.common.did_you_know" />${baseUri}/about/news

<@emailMacros.msg "email.common.if_you_have_any1" />http://support.orcid.org<@emailMacros.msg "email.common.if_you_have_any2" />

<@emailMacros.msg "email.common.kind_regards" />
${baseUri}/home?lang=${locale}

<@emailMacros.msg "email.common.you_have_received_this_email" />
<#include "email_footer.ftl"/>