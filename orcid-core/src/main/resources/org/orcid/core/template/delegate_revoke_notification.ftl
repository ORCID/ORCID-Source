<#import "email_macros.ftl" as emailMacros />

<#include "notification_header.ftl"/>
<@emailMacros.msg "notification.share.record" />

${emailNameForDelegate}<@emailMacros.space /><a style="text-decoration: underline;color: #085c77;display: inline-block;" href="${baseUri}/${orcidValueForDelegate}" target="_blank">
                (${baseUri}/${orcidValueForDelegate})
            </a> <@emailMacros.space /><@emailMacros.msg "notification.delegate.receipt.revokeTrustedIndividual" />.

<@emailMacros.msg "notification.delegate.receipt.revokeAccessYourRecord" /><@emailMacros.space /><a style="text-decoration: underline;color: #085c77;display: inline-block;" href="${baseUri}/trusted-parties" target="_blank">
                <@emailMacros.msg "notification.delegate.receipt.trustedIndividuals" />
            </a>
