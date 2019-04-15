<#import "email_macros.ftl" as emailMacros />

<@emailMacros.msg "email.reactivation.thank_you" />

<@emailMacros.msg "email.reactivation.to_reactivate" />

${reactivationUrl}

<@emailMacros.msg "email.reactivation.after" />

<@emailMacros.msg "email.common.if_you_have_any1" /> <a href='<@emailMacros.msg "email.common.need_help.description.2.href" />' target="orcid.contact_us"><@emailMacros.msg "email.common.need_help.description.2.href" /></a><@emailMacros.msg "email.common.if_you_have_any2" />

<@emailMacros.msg "email.common.warm_regards" />
<a href='<@emailMacros.msg "email.common.need_help.description.2.href" />' target="orcid.contact_us"><@emailMacros.msg "email.common.need_help.description.2.href" /></a>


${baseUri}/home?lang=${locale}

<@emailMacros.msg "email.common.you_have_received_this_email" />
<#include "email_footer.ftl"/>
