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

<@emailMacros.msg "email.welcome.thank_you_for_creating" /><@emailMacros.space />${orcidId}<@emailMacros.space /><@emailMacros.msg "email.welcome.full_orcid_and_link_public" /><@emailMacros.space />${baseUri}/${orcidId}

<@emailMacros.msg "email.welcome.please_verify_your_email" />

<@emailMacros.msg "email.button" />

${verificationUrl}?lang=${locale}

<@emailMacros.msg "email.welcome.please_visit_your" /><@emailMacros.space /><@emailMacros.msg "email.welcome.researcher_homepage" /><@emailMacros.space /><@emailMacros.msg "email.welcome.for_more_information" />

<#include "email_footer.ftl"/>
