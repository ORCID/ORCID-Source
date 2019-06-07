<#import "email_macros.ftl" as emailMacros />
<@emailMacros.msg "email.reset_password.sorry" />

<@emailMacros.msg "email.reset_password.orcid_id" /> ${submittedEmail} <@emailMacros.msg "email.reset_password.is" /> ${baseUri}/${orcid}

<@emailMacros.msg "email.reset_password.to_reset" />

${passwordResetUrl}

<@emailMacros.msg "email.reset_password.note" />

<@emailMacros.msg "email.reset_password.if_you_did_not" />

<@emailMacros.msg "email.common.if_you_have_any1" /> <@emailMacros.msg "email.common.need_help.description.2.href" /><@emailMacros.msg "email.common.if_you_have_any2" />

<@emailMacros.msg "email.common.warm_regards" />
<@emailMacros.msg "email.common.need_help.description.2.href" />


${baseUri}/home?lang=${locale}

<@emailMacros.msg "email.common.you_have_received_this_email" />
<#include "email_footer.ftl"/>
