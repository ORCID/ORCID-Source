<#import "email_macros.ftl" as emailMacros />

<#include "notification_header.ftl"/>
<@emailMacros.msg "notification.delegate.receipt.record" />

${emailNameGrantingPermission}<@emailMacros.space /><@emailMacros.msg "notification.delegate.receipt.trustedIndividual" />

<@emailMacros.msg "notification.delegate.receipt.accountDelegate" /><@emailMacros.space />${emailNameGrantingPermission}<@emailMacros.space /><@emailMacros.msg "notification.delegate.asTrustedIndividual" />

<@emailMacros.msg "notification.delegate.receipt.accountDelegateMeans" /><@emailMacros.space />${emailNameGrantingPermission}<@emailMacros.space /><@emailMacros.msg "notification.delegate.receipt.orcidRecord" />

<@emailMacros.msg "notification.delegate.receipt.tutorial" /><@emailMacros.space /><@emailMacros.msg "notification.delegate.receipt.tutorialLink" />

<@emailMacros.msg "notification.delegate.receipt.questions" /><@emailMacros.space />${emailNameGrantingPermission}<@emailMacros.space /><@emailMacros.msg "notification.delegate.receipt.helpDesk" />

<#include "notification_footer.ftl"/>