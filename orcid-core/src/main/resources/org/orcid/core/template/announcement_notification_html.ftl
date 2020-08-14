<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
    <!DOCTYPE html>
    <html>
    <head>
        <title>${subject}</title>
        <#include "css/notification.css">
    </head>
    <body>
    <div class="main">
        <#include "notification_header_html.ftl"/>
        <hr class="title" />
        <p class="your-record"><@emailMacros.msg "notification.announcement" /></p>
        <p><@emailMacros.msg "notification.announcement.orcid" /></p>
        <hr class="title" />
        <p>
            <@emailMacros.msg "notification.announcement.text" />
        <p>
        <#include "notification_footer_html.ftl"/>
    </div>
    </body>
    </html>
</#escape>
