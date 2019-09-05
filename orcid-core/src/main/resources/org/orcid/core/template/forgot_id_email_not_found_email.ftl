<#import "email_macros.ftl" as emailMacros />
<@emailMacros.msg "email.common.dear" /><@emailMacros.space />${submittedEmail}<@emailMacros.msg "email.common.dear.comma" />

<@emailMacros.msg "email.forgotten_id.could_not_find" />

${submittedEmail}

<@emailMacros.msg "email.forgotten_id.you_can" /><@emailMacros.space /><@emailMacros.msg "email.forgotten_id.try_another_email" /><@emailMacros.space />(${baseUri}/signin)<@emailMacros.msg "email.forgotten_id.or_register" />
${baseUri}/signin

<@emailMacros.msg "email.forgotten_id.no_access" /><@emailMacros.space /><@emailMacros.msg "email.common.if_you_have_any.contact_us" /><@emailMacros.space />(<@emailMacros.msg "email.common.need_help.description.2.href" />)<@emailMacros.msg "email.common.if_you_have_any2" />

<@emailMacros.msg "email.common.warm_regards" />
${baseUri}/home?lang=${locale}

<@emailMacros.msg "email.common.you_have_received_this_email" />
<#include "email_footer.ftl"/>
