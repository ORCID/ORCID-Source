<#import "email_macros.ftl" as emailMacros />

<@emailMacros.msg "email.welcome.your_id.id" /> ${orcid}
<@emailMacros.msg "email.welcome.your_id.link" />:<@emailMacros.space /><a href="${baseUri}/${orcid}" target="orcid.blank">${baseUri}/${orcid}</a>

<@emailMacros.msg "email.changes.we_have_detected_a_change" />
<br>
<#if (emailListChange.addedEmails?has_content)>
<#list emailListChange.addedEmails as email>
<@emailMacros.msg "email.changes.the_email_address" /> <a href="mailto:${email.getValue()}">${email.getValue()}</a> <@emailMacros.msg "email.changes.has_been_added" />
</#list>
</#if>
<#if (emailListChange.removedEmails?has_content)>
    <#list emailListChange.removedEmails as email>
        <@emailMacros.msg "email.changes.the_email_address" /> <a href="mailto:${email.getEmail()}">${email.getEmail()}</a> <@emailMacros.msg "email.changes.has_been_removed" />
    </#list>
</#if>

    <@emailMacros.msg "email.security.note.shared.1" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.2" /><@emailMacros.space />&mdash;<@emailMacros.space /><@emailMacros.msg "email.security.note.shared.3" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.4" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.5" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.take_action" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.actions.action.1" /><@emailMacros.space /><a href="https://orcid.org/reset-password" rel="noopener noreferrer" target="_blank"><@emailMacros.msg "email.security.note.shared.actions.action.2" /></a><@emailMacros.msg "email.common.comma" /><@emailMacros.space /><@emailMacros.msg "email.security.note.actions.remove_emails" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.actions.support.1" /><@emailMacros.space /><a href="https://support.orcid.org/hc/en-us/requests/new" rel="noopener noreferrer" target="_blank"><@emailMacros.msg "email.security.note.shared.actions.support.2" /></a><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.actions.support.3" />

<#include "email_footer_security.ftl"/>