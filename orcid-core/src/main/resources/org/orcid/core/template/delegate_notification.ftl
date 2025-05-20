<#import "email_macros.ftl" as emailMacros />

<#include "notification_header.ftl"/>
<@emailMacros.msg "notification.share.record" />

${emailNameForDelegate}<@emailMacros.space /><@emailMacros.msg "notification.delegate.trustedIndividual" />

<@emailMacros.msg "notification.delegate.added" /><@emailMacros.space />${emailNameForDelegate}<@emailMacros.space /><@emailMacros.msg "notification.delegate.asTrustedIndividual" />

<@emailMacros.msg "notification.delegate.accessYourRecord" /><@emailMacros.space /><@emailMacros.msg "notification.delegate.receipt.trustedParties" /> (${baseUri}/trusted-parties).
<#include "notification_footer.ftl"/>
