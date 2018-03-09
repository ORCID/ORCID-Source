<#import "email_macros.ftl" as emailMacros />
<@emailMacros.msg "email.common.dear" /><@emailMacros.space />${emailName}<@emailMacros.msg "email.common.dear.comma" />

<#if isPrimary?? && isPrimary><@emailMacros.msg "email.verify.primary_reminder" /><@emailMacros.space /></#if><@emailMacros.msg "email.verify.thank_you" />

${verificationUrl}?lang=${locale}

<#if features["HTTPS_IDS"]?? && features["HTTPS_IDS"]>
<@emailMacros.msg "email.verify.1" /><@emailMacros.space />${orcid}<@emailMacros.msg "email.verify.2" /><@emailMacros.space />${baseUri}/${orcid}?lang=${locale}<@emailMacros.space /><@emailMacros.msg "email.verify.primary_email_1" /><@emailMacros.space />${primaryEmail}<@emailMacros.msg "email.verify.primary_email_2" />.
<#else>
<@emailMacros.msg "email.verify.1" /><@emailMacros.space />${orcid}<@emailMacros.msg "email.verify.2" /><@emailMacros.space />${baseUriHttp}/${orcid}?lang=${locale}<@emailMacros.space /><@emailMacros.msg "email.verify.primary_email_1" /><@emailMacros.space />${primaryEmail}<@emailMacros.msg "email.verify.primary_email_2" />.
</#if> 
 
<@emailMacros.msg "email.verify.if_you_did_not" />

<@emailMacros.msg "email.common.did_you_know" /><@emailMacros.space />${baseUri}/about/news

<@emailMacros.msg "email.common.if_you_have_any1" /><@emailMacros.knowledgeBaseUri /><@emailMacros.msg "email.common.if_you_have_any2" />

<@emailMacros.msg "email.common.kind_regards" />
${baseUri}/home?lang=${locale}

<@emailMacros.msg "email.common.you_have_received_this_email" />
<#include "email_footer.ftl"/>
