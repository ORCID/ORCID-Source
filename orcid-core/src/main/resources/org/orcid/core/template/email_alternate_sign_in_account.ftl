<#import "email_macros.ftl" as emailMacros />
<@emailMacros.msg "email.welcome.your_id.id" /> ${orcid}

<@emailMacros.msg "email.alternate_sign_in.security.1" />

<#if (accountWasAdded) ?? && accountWasAdded == true>
    <@emailMacros.msg "email.alternate_sign_in.security.added" /> ${alternateAccount}
<#else>
    <@emailMacros.msg "email.alternate_sign_in.security.removed" /> ${alternateAccount}
</#if>

<strong><@emailMacros.msg "email.security.note.shared.1" /></strong><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.2" /><@emailMacros.space />&mdash;<@emailMacros.space /><@emailMacros.msg "email.security.note.shared.3" /><@emailMacros.space /><strong><i><@emailMacros.msg "email.security.note.shared.4" /></i></strong><@emailMacros.space /><@emailMacros.msg "email.security.pwd.note.5" /><@emailMacros.space /><strong><@emailMacros.msg "email.security.note.shared.take_action" /></strong><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.actions.action.1" /><@emailMacros.space /><a href="https://orcid.org/reset-password" rel="noopener noreferrer" target="_blank"><@emailMacros.msg "email.security.note.shared.actions.action.2" /></a><@emailMacros.space /><@emailMacros.msg "email.alternate_sign_in.security.action.3" /><@emailMacros.msg "email.common.period" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.actions.support.1" /><@emailMacros.space /><a href="https://support.orcid.org/hc/en-us/requests/new" rel="noopener noreferrer" target="_blank"><@emailMacros.msg "email.security.note.shared.actions.support.2" /></a><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.actions.support.3" />

<#include "email_footer_security_html.ftl"/>
