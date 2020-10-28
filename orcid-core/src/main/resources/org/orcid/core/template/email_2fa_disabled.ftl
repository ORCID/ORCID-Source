<#import "email_macros.ftl" as emailMacros />
<@emailMacros.msg "email.common.dear" /><@emailMacros.space />${emailName}<@emailMacros.msg "email.common.dear.comma" />

<@emailMacros.msg "email.2fa_disabled.two_factor_auth_disabled" />

<@emailMacros.msg "email.2fa_disabled.if_you_received" />

<@emailMacros.msg "email.2fa_disabled.no_reply" />

<@emailMacros.msg "email.2fa_disabled.more_info"  />

<@emailMacros.msg "email.common.warm_regards" />

<@emailMacros.msg "email.common.need_help.description.2.href" />

${baseUri}/home?lang=${locale}

<@emailMacros.msg "email.common.you_have_received_this_email" />
<#include "email_footer.ftl"/>
