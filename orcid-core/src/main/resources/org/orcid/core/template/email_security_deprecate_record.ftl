<#import "email_macros.ftl" as emailMacros />

<@emailMacros.msg "email.deprecate.security.your_orcid_record" /><@emailMacros.space />${deprecated_orcid}<@emailMacros.space /><@emailMacros.msg "email.deprecate.security.has_been_deprecated" /><@emailMacros.space />${orcid}.


<@emailMacros.msg "email.deprecate.security.all_information_deleted" /><@emailMacros.space /><a href="${baseUri}/${orcid}" target="orcid.blank">${baseUri}/${orcid}</a>

<#list emailList as email>
       <a href="mailto:${email.getEmail()}">${email.getEmail()}</a><br />
 </#list>
 
<@emailMacros.msg "email.security.note.shared.1" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.2" /><@emailMacros.space />&mdash;<@emailMacros.space /><@emailMacros.msg "email.security.note.shared.3" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.4" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.5" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.take_action" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.actions.action.1" /><@emailMacros.space /><a href="https://orcid.org/reset-password" rel="noopener noreferrer" target="_blank"><@emailMacros.msg "email.security.note.shared.actions.action.2" /></a><@emailMacros.msg "email.common.period" /><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.actions.support.1" /><@emailMacros.space /><a href="https://support.orcid.org/hc/en-us/requests/new" rel="noopener noreferrer" target="_blank"><@emailMacros.msg "email.security.note.shared.actions.support.2" /></a><@emailMacros.space /><@emailMacros.msg "email.security.note.shared.actions.support.3" />

<#include "email_footer_security.ftl"/>
