<#import "email_macros.ftl" as emailMacros />

<#include "notification_header.ftl"/>
<@emailMacros.msg "notification.announcement" />
<@emailMacros.msg "notification.announcement.orcid" />

<@emailMacros.msg "notification.announcement.text" />

<#include "notification_footer.ftl"/>
