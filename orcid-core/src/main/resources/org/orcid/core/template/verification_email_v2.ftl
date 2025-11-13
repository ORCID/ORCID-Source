<#import "email_macros.ftl" as emailMacros />
<@emailMacros.msg "email.welcome.your_id.id" /><@emailMacros.space />${orcidId}
<@emailMacros.msg "email.welcome.your_id.link" /><@emailMacros.space />${baseUri}/${orcidId}

<@emailMacros.msg "email.verify.hi" /><@emailMacros.space />${userName},
 <#if isPrimary?? && isPrimary><@emailMacros.msg "email.verify.primary.reminder" /><#else><@emailMacros.msg "email.verify.alternate.reminder" /></#if>


<#include "how_do_i_verify_my_email_address.ftl"/>

<#if isPrimary?? && isPrimary>
<@emailMacros.msg "email.welcome.please_visit_our" /><@emailMacros.space /><a href="https://info.orcid.org/researchers/" target="orcid.blank"><@emailMacros.msg "email.welcome.researcher_homepage" /></a><@emailMacros.space /><@emailMacros.msg "email.welcome.for_more_information" />
</#if>
<#include "email_footer.ftl"/>