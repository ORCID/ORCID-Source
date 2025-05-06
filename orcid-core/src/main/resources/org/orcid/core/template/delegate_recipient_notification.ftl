<#import "email_macros.ftl" as emailMacros />

<#include "notification_header.ftl"/>
<@emailMacros.msg "notification.share.record" />

${emailNameGrantingPermission}<@emailMacros.space /><@emailMacros.msg "notification.delegate.receipt.trustedIndividual" />

<@emailMacros.msg "notification.delegate.receipt.accountDelegate" /><@emailMacros.space />${emailNameGrantingPermission}<@emailMacros.space /><@emailMacros.msg "notification.delegate.asTrustedIndividual" />

<@emailMacros.msg "notification.delegate.receipt.accountDelegateMeans" /><@emailMacros.space />${emailNameGrantingPermission}<@emailMacros.space /><@emailMacros.msg "notification.delegate.receipt.orcidRecord" /><@emailMacros.space /><@emailMacros.msg "notification.delegate.receipt.revokeAnytime" /><@emailMacros.space /><a style="text-decoration: underline;color: #085c77;display: inline-block;" href="${baseUri}/trusted-parties" target="_blank"><@emailMacros.msg "notification.delegate.receipt.trustedParties" /></a><@emailMacros.space /><@emailMacros.msg "notification.delegate.receipt.sectionOf" />

<@emailMacros.msg "notification.delegate.receipt.tutorial" /><@emailMacros.space />https://support.orcid.org/hc/articles/360006973613.

<@emailMacros.msg "notification.delegate.receipt.questions" /><@emailMacros.space />${emailNameGrantingPermission}<@emailMacros.space /><@emailMacros.msg "notification.delegate.receipt.helpDesk" />

<#include "notification_footer.ftl"/>