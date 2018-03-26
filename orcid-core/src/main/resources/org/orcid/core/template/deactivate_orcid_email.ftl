<#import "email_macros.ftl" as emailMacros />
<@emailMacros.msg "email.common.dear" /><@emailMacros.space />${emailName}<@emailMacros.msg "email.common.dear.comma" />

<@emailMacros.msg "email.deactivate.you_have_requested.1" /> 
<#if features["HTTPS_IDS"]?? && features["HTTPS_IDS"]>
${baseUri}/${orcid}?lang=${locale}<@emailMacros.msg "email.deactivate.you_have_requested.2" />
<#else>
${baseUriHttp}/${orcid}?lang=${locale}<@emailMacros.msg "email.deactivate.you_have_requested.2" />
</#if>
${baseUri}${deactivateUrlEndpoint}?lang=${locale}


<@emailMacros.msg "email.deactivate.once_an_account" />

<@emailMacros.msg "email.deactivate.if_you_did" />
<@emailMacros.msg "email.deactivate.support_email" />

<@emailMacros.msg "email.common.kind_regards" />
${baseUri}/home?lang=${locale}

<@emailMacros.msg "email.common.you_have_received_this_email" />
<#include "email_footer.ftl"/>
