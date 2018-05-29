<#import "email_macros.ftl" as emailMacros />
<@emailMacros.msg "email.common.dear" /><@emailMacros.space />${emailName}<@emailMacros.msg "email.common.dear.comma" />

<#if features["GDPR_DEACTIVATE"]?? && features["GDPR_DEACTIVATE"]>

<@emailMacros.msg "email.deactivate.gdpr_you_have_asked.1" />
${baseUri}/${orcid}?lang=${locale}

<@emailMacros.msg "email.deactivate.you_have_requested.2" />

${baseUri}${deactivateUrlEndpoint}?lang=${locale}

<@emailMacros.msg "email.deactivate.gdpr_if_you_do_not" />

<@emailMacros.msg "email.deactivate.gdpr_please_note" />

<@emailMacros.msg "email.deactivate.gdpr_once_you_have" />

<@emailMacros.msg "email.deactivate.gdpr_if_you_would" /> <@emailMacros.msg "email.deactivate.gdpr_orcid_support" />

${baseUri}/help/contact-us

<@emailMacros.msg "email.deactivate.gdpr_to_request_removal" />

${baseUri}/${orcid} <@emailMacros.msg "email.deactivate.gdpr_at_any_point" />

<#else>

<@emailMacros.msg "email.deactivate.you_have_requested.1" />
${baseUri}/${orcid}?lang=${locale}

<@emailMacros.msg "email.deactivate.you_have_requested.2" />

${baseUri}${deactivateUrlEndpoint}?lang=${locale}

<@emailMacros.msg "email.deactivate.once_an_account" />

<@emailMacros.msg "email.deactivate.if_you_did" />
<@emailMacros.msg "email.deactivate.support_email" />

</#if> 

<@emailMacros.msg "email.common.kind_regards" />
${baseUri}/home?lang=${locale}

<@emailMacros.msg "email.common.you_have_received_this_email" />
<#include "email_footer.ftl"/>
