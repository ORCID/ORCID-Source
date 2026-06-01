<#import "email_macros.ftl" as emailMacros />

<@emailMacros.msg "email.welcome.your_id.id" /> ${orcid}
<@emailMacros.msg "email.welcome.your_id.link" />:<@emailMacros.space /><a href="${baseUri}/${orcid}" target="orcid.blank">${baseUri}/${orcid}</a>

<@emailMacros.msg "email.reset_password.security.your_orcid_account_password" />

<@emailMacros.msg "email.security.note.shared.1" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.2" /><@emailMacros.space />&mdash;<@emailMacros.space /><@emailMacros.msg "email.security.note.shared.3" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.4" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.5" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.take_action" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.actions.action.1" /><@emailMacros.space /><a href="https://orcid.org/reset-password" rel="noopener noreferrer" target="_blank"><@emailMacros.msg "email.security.note.shared.actions.action.2" /></a><@emailMacros.msg "email.common.period" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.actions.support.1" /><@emailMacros.space /><a href="https://support.orcid.org/hc/en-us/requests/new" rel="noopener noreferrer" target="_blank"><@emailMacros.msg "email.security.note.shared.actions.support.2" /></a><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.actions.support.3" />

<#include "email_footer_security.ftl"/>

