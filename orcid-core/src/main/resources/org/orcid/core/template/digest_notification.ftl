<#import "email_macros.ftl" as emailMacros />

<#include "notification_header.ftl"/>
<@emailMacros.msg "notification.digest.record" />
${emailName}<@emailMacros.space /><@emailMacros.msg "notification.digest.hasChanges" />

<@emailMacros.msg "notification.digest.showing" /><@emailMacros.space />${emailName}<@emailMacros.space /><@emailMacros.msg "notification.digest.outOf" />${emailName}<@emailMacros.space /><@emailMacros.msg "notification.digest.changes" />

<#include "notification_footer.ftl"/>
