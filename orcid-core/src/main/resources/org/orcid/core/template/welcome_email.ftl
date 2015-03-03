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
<@emailMacros.msg "email.common.dear" /> ${emailName}<@emailMacros.msg "email.common.dear.comma" />

<@emailMacros.msg "email.welcome.thank_you.1" /> ${source_name_if_exists} <@emailMacros.msg "email.welcome.thank_you.2" />

${verificationUrl}?lang=${locale}

* <@emailMacros.msg "email.welcome.your_id.id" /> ${orcidId}
* <@emailMacros.msg "email.welcome.your_id.link" /> ${baseUri}/${orcidId}

<@emailMacros.msg "email.welcome.next_steps" />

<@emailMacros.msg "email.welcome.next_steps.1" />

<@emailMacros.msg "email.welcome.next_steps.1.description.1.1" /> ${baseUri}/my-orcid <@emailMacros.msg "email.welcome.next_steps.1.description.1.2" />
<@emailMacros.msg "email.welcome.next_steps.1.description.2" />

<@emailMacros.msg "email.welcome.next_steps.1.description.tips" />

<@emailMacros.msg "email.welcome.next_steps.2" />

<@emailMacros.msg "email.welcome.next_steps.2.description" />

<@emailMacros.msg "email.welcome.need_help" />

<@emailMacros.msg "email.welcome.need_help.description" />

<@emailMacros.msg "email.common.kind_regards" />
${baseUri}

<@emailMacros.msg "email.api_record_creation.you_have_received.1" />${baseUri}/home?lang=${locale}<@emailMacros.msg "email.api_record_creation.you_have_received.2" />
<#include "email_footer.ftl"/>