<#import "email_macros.ftl" as emailMacros />

<#include "notification_header.ftl"/>
<@emailMacros.msg "notification.share.record" />

${emailNameForDelegate}<@emailMacros.space />(${baseUri}/${orcidValueForDelegate}) <@emailMacros.space /><@emailMacros.msg "notification.delegate.receipt.revokeTrustedIndividual" />.

<@emailMacros.msg "notification.delegate.receipt.revokeAccessYourRecord" /><@emailMacros.space /><@emailMacros.msg "notification.delegate.receipt.trustedIndividuals" /> (${baseUri}/trusted-parties)
<@emailMacros.msg "notification.delegate.receipt.revokeConcerns" /><@emailMacros.space />
https://orcid.org/help/contact-us.