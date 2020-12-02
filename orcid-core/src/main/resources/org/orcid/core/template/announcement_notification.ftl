<#import "email_macros.ftl" as emailMacros />

<#include "notification_header.ftl"/>
<@emailMacros.msg "notification.share.announcement" />
<@emailMacros.msg "notification.share.announcement.orcid" />

<@emailMacros.msg "notification.share.announcement.text" />

<#include "notification_footer.ftl"/>
