<#import "email_macros.ftl" as emailMacros />

<@emailMacros.msg "email.reactivation.thank_you" />

<@emailMacros.msg "email.reactivation.to_reactivate" />

${reactivationUrl}

<@emailMacros.msg "email.reactivation.after" />

<@emailMacros.msg "email.common.if_you_have_any1" /> <@emailMacros.msg "email.common.need_help.description.2.href" /><@emailMacros.msg "email.common.if_you_have_any2" />

<@emailMacros.msg "email.common.kind_regards" />
${baseUri}/home?lang=${locale}

<@emailMacros.msg "email.common.you_have_received_this_email" />
<#include "email_footer.ftl"/>
