<#import "email_macros.ftl" as emailMacros />
<@emailMacros.msg "email.common.dear" /><@emailMacros.space />${emailName}<@emailMacros.msg "email.common.dear.comma" />

<@emailMacros.msg "email.reset_password.sorry" />

<@emailMacros.msg "email.reset_password.orcid_id" /> ${submittedEmail} <@emailMacros.msg "email.reset_password.is" /> ${baseUri}/${orcid}

<@emailMacros.msg "email.reset_password.to_reset" />

${passwordResetUrl}

<@emailMacros.msg "email.reset_password.note" />

<@emailMacros.msg "email.reset_password.if_you_did_not" />

<@emailMacros.msg "email.common.if_you_have_any1" /> <a href='<@emailMacros.msg "email.common.need_help.description.2.href" />' target="orcid.contact_us"><@emailMacros.msg "email.common.need_help.description.2.href" /></a><@emailMacros.msg "email.common.if_you_have_any2" />

<@emailMacros.msg "email.common.warm_regards" />
<a href='<@emailMacros.msg "email.common.need_help.description.2.href" />' target="orcid.contact_us"><@emailMacros.msg "email.common.need_help.description.2.href" /></a>


${baseUri}/home?lang=${locale}

<@emailMacros.msg "email.common.you_have_received_this_email" />
<#include "email_footer.ftl"/>
