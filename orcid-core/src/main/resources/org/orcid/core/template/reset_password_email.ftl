<#import "email_macros.ftl" as emailMacros />
<@emailMacros.msg "email.common.dear" /><@emailMacros.space />${emailName}<@emailMacros.msg "email.common.dear.comma" />

<@emailMacros.msg "email.reset_password.sorry" /> ${baseUri}/${orcid}

<@emailMacros.msg "email.reset_password.to_reset" />

${passwordResetUrl}

<@emailMacros.msg "email.reset_password.note" />

<@emailMacros.msg "email.reset_password.after" />

<@emailMacros.msg "email.common.if_you_have_any1" /><@emailMacros.knowledgeBaseUri /><@emailMacros.msg "email.common.if_you_have_any2" />

<@emailMacros.msg "email.common.kind_regards" />
${baseUri}/home?lang=${locale}

<@emailMacros.msg "email.common.you_have_received_this_email" />
<#include "email_footer.ftl"/>
