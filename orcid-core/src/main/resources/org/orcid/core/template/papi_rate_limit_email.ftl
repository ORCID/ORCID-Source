<#import "email_macros.ftl" as emailMacros />
<@emailMacros.msg "email.common.dear" /><@emailMacros.space />${emailName}, 

<@emailMacros.msg "papi.rate.limit.important.msg" /><@emailMacros.space /><@emailMacros.msg "papi.rate.limit.daily.usage.limit" /><@emailMacros.space /><@emailMacros.msg "papi.rate.limit.your.integration" /><@emailMacros.space />(https://info.orcid.org/ufaqs/what-are-the-api-limits/):

<@emailMacros.msg "papi.rate.limit.client.name" /><@emailMacros.space />${clientName}
<@emailMacros.msg "papi.rate.limit.client.id" /><@emailMacros.space />${clientId}

<@emailMacros.msg "papi.rate.limit.please.remember" /><@emailMacros.space /><@emailMacros.msg "papi.rate.limit.public.api.terms" /><@emailMacros.space />(https://info.orcid.org/public-client-terms-of-service).<@emailMacros.space /><@emailMacros.msg "papi.rate.limit.by.non.commercial" />

<@emailMacros.msg "papi.rate.limit.based.on.your" /><@emailMacros.space /><@emailMacros.msg "papi.rate.limit.member.api" /><@emailMacros.space />(https://info.orcid.org/what-is-orcid/services/member-api/).<@emailMacros.space /><@emailMacros.msg "papi.rate.limit.not.only" />

<@emailMacros.msg "papi.rate.limit.to.minimize" />

<#include "email_footer.ftl"/>
