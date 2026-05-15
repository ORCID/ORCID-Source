<#import "email_macros.ftl" as emailMacros />

<@emailMacros.msg "email.welcome.your_id.id" /> ${orcid}

<@emailMacros.msg "email.deactivate.your_orcid_account_has_been_deactivated" />

<@emailMacros.msg "email.security.note.shared.1" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.2" /><@emailMacros.space />&mdash;<@emailMacros.space /><@emailMacros.msg "email.security.note.shared.3" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.4" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.5" /><@emailMacros.space /><@emailMacros.msg "email.security.deactivate.note.actions.1" /><@emailMacros.space /><@emailMacros.msg "email.security.deactivate.note.actions.2" /><@emailMacros.space /><@emailMacros.msg "email.security.deactivate.note.actions.3" /><@emailMacros.space /><a href="https://support.orcid.org/hc/en-us/requests/new" rel="noopener noreferrer" target="_blank"><@emailMacros.msg "email.security.note.shared.contact_support" /></a>

<#include "email_footer_security.ftl"/>