<#import "email_macros.ftl" as emailMacros />

<@emailMacros.msg "email.welcome.your_id.id" /> ${orcid}


<@emailMacros.msg "email.2fa_enabled.two_factor_enabled" />


<@emailMacros.msg "email.common.need_help.description.2.href" />

<@emailMacros.msg "email.security.note.shared.1" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.2" /><@emailMacros.space />&mdash;<@emailMacros.space /><@emailMacros.msg "email.security.note.shared.3" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.4" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.5" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.take_action" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.actions.support.1" /><@emailMacros.space /><a href="https://support.orcid.org/hc/en-us/requests/new" rel="noopener noreferrer" target="_blank"><@emailMacros.msg "email.security.note.shared.contact_support" /></a>

${baseUri}/home?lang=${locale}


<#include "email_footer_security.ftl"/>
