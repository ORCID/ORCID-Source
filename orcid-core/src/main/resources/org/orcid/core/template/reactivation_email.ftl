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

<@emailMacros.msg "email.reactivation.thank_you" />

<@emailMacros.msg "email.reactivation.to_reactivate" />

${reactivationUrl}

<@emailMacros.msg "email.reactivation.after" />

<@emailMacros.msg "email.common.if_you_have_any1" />http://support.orcid.org<@emailMacros.msg "email.common.if_you_have_any2" />

<@emailMacros.msg "email.common.kind_regards" />
${baseUri}/home?lang=${locale}

<@emailMacros.msg "email.common.you_have_received_this_email" />
<#include "email_footer.ftl"/>
