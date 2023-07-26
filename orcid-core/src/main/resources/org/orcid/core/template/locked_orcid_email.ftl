<#import "email_macros.ftl" as emailMacros />
<@emailMacros.msg "email.common.dear" /><@emailMacros.space />${emailName}<@emailMacros.msg "email.common.dear.comma" />

<#if features["HTTPS_IDS"]?? && features["HTTPS_IDS"]>
<@emailMacros.msg "email.locked.this_is_an_important_message.1" />${baseUri}/${orcid}?lang=${locale}<@emailMacros.msg "email.locked.this_is_an_important_message.2" /> 
<#else>
<@emailMacros.msg "email.locked.this_is_an_important_message.1" />${baseUriHttp}/${orcid}?lang=${locale}<@emailMacros.msg "email.locked.this_is_an_important_message.2" />
</#if>

<@emailMacros.msg "email.locked.orcid_registry_provides_identifiers" /><@emailMacros.space /><@emailMacros.msg "email.locked.please_see" /><@emailMacros.space /><a href="https://info.orcid.org/terms-of-use/"><@emailMacros.msg "email.locked.terms_of_use" /></a><@emailMacros.space /><@emailMacros.msg "email.locked.further_information" />

<#include "email_footer.ftl"/>
