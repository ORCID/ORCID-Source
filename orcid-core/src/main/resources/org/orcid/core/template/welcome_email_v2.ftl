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

<@emailMacros.msg "email.common.dear" /><@emailMacros.space />${emailName}<@emailMacros.msg "email.common.dear.comma" />

<@emailMacros.msg "email.welcome.thank_you.1" />

${verificationUrl}?lang=${locale}

<@emailMacros.msg "email.welcome.your_id.id" /><@emailMacros.space />${orcidId}<@emailMacros.msg "email.welcome.your_id.link" /><@emailMacros.space />${baseUri}/${orcidId}

<@emailMacros.msg "email.welcome.next_steps" />

<@emailMacros.msg "email.welcome.next_steps.1" />
		
<@emailMacros.msg "email.welcome.next_steps.1.description.1.1" /><@emailMacros.space />${baseUri}/my-orcid<@emailMacros.space /><@emailMacros.msg "email.welcome.next_steps.1.description.1.2" /><@emailMacros.space />

<@emailMacros.msg "email.welcome.next_steps.1.description.2" />

<@emailMacros.msg "email.welcome.next_steps.1.description.tips.1" /><@emailMacros.space /><@emailMacros.msg "email.welcome.next_steps.1.description.tips.1.link.text" /><@emailMacros.space /><@emailMacros.msg "email.welcome.next_steps.1.description.tips.1.link.href" /><@emailMacros.msg "email.welcome.next_steps.1.description.tips.2" /><@emailMacros.space /><@emailMacros.msg "email.welcome.next_steps.1.description.link.text" /><@emailMacros.msg "email.welcome.next_steps.1.description.link.href" />

<@emailMacros.msg "email.welcome.next_steps.2" />
		
<@emailMacros.msg "email.welcome.next_steps.2.description" />

<@emailMacros.msg "email.welcome.need_help" />

<@emailMacros.msg "email.common.need_help.description.1" /><@emailMacros.space /><@emailMacros.msg "email.common.need_help.description.1.text" /><@emailMacros.space /><@emailMacros.msg "email.common.need_help.description.2" /><a href='<@emailMacros.msg "email.common.need_help.description.2.href" />'><@emailMacros.msg "email.common.need_help.description.2.text" /></a>

<@emailMacros.msg "email.common.warm_regards.simple" />
${baseUri}/home?lang=${locale}

<@emailMacros.msg "email.common.you_have_received_this_email" />
<#include "email_footer.ftl"/>