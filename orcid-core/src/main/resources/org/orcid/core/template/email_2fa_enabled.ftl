<#import "email_macros.ftl" as emailMacros />

<@emailMacros.msg "email.welcome.your_id.id" /> ${orcid}

<@emailMacros.msg "email.2fa_enabled.two_factor_enabled" />

<@emailMacros.msg "email.common.need_help.description.2.href" />

${baseUri}/home?lang=${locale}

<#include "email_footer.ftl"/>
