<#import "email_macros.ftl" as emailMacros />

<@emailMacros.msg "email.deprecate.security.your_orcid_record" /><@emailMacros.space />${deprecated_orcid}<@emailMacros.space /><@emailMacros.msg "email.deprecate.security.has_been_deprecated" /><@emailMacros.space />${orcid}.


<@emailMacros.msg "email.deprecate.security.all_information_deleted" /><@emailMacros.space /><a href="${baseUri}/${orcid}" target="orcid.blank">${baseUri}/${orcid}</a>

<#list emailList as email>
       <a href="mailto:${email.getEmail()}">${email.getEmail()}</a><br />
 </#list>
 
<#include "email_footer_security.ftl"/>
