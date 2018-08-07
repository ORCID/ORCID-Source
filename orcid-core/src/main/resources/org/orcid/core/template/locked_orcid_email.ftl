<#import "email_macros.ftl" as emailMacros />
<@emailMacros.msg "email.common.dear" /><@emailMacros.space />${emailName}<@emailMacros.msg "email.common.dear.comma" />

<#if features["HTTPS_IDS"]?? && features["HTTPS_IDS"]>
<@emailMacros.msg "email.locked.this_is_an_important_message.1" />${baseUri}/${orcid}?lang=${locale}<@emailMacros.msg "email.locked.this_is_an_important_message.2" /> 
<#else>
<@emailMacros.msg "email.locked.this_is_an_important_message.1" />${baseUriHttp}/${orcid}?lang=${locale}<@emailMacros.msg "email.locked.this_is_an_important_message.2" />
</#if>

<@emailMacros.msg "email.locked.the_orcid_registry_provides" />

<@emailMacros.msg "email.locked.if_you_believe" />

<@emailMacros.msg "email.common.warm_regards" />
<@emailMacros.msg "email.common.need_help.description.2.href" />


${baseUri}/home?lang=${locale}

<@emailMacros.msg "email.common.you_have_received_this_email" />
<#include "email_footer.ftl"/>
