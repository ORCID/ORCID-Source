<#import "email_macros.ftl" as emailMacros />

<@emailMacros.msg "email.2fa_disabled.two_factor_disabled" />

<@emailMacros.msg "email.common.need_help.description.2.href" />

${baseUri}/home?lang=${locale}

<#include "email_footer.ftl"/>
