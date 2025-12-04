<#import "email_macros.ftl" as emailMacros />

<@emailMacros.msg "email.welcome.your_id.id" /> ${orcid}
<@emailMacros.msg "email.welcome.your_id.link" />:<@emailMacros.space /><a href="${baseUri}/${orcid}" target="orcid.blank">${baseUri}/${orcid}</a>

<@emailMacros.msg "email.changes.we_have_detected" />
<br>
<#if (emailListChange.verifiedEmails?has_content)>
<#list emailListChange.verifiedEmails as email>
<@emailMacros.msg "email.changes.the_email_address" /> <a href="mailto:${email}">${email}</a> <@emailMacros.msg "email.changes.has_been_verified" />
</#list>
</#if>
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

<@emailMacros.msg "email.security.if_you_did_not_make_these_changes" /> <a href="https://orcid.org/reset-password" rel="noopener noreferrer" target="_blank"><@emailMacros.msg "email.security.update_your_password" /></a> <@emailMacros.msg "email.changes.and_remove" />

<#include "email_footer_security.ftl"/>