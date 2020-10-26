<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
    <!DOCTYPE html>
    <html>
    <head>
        <title>${subject}</title>
        <#include "css/notification.css">
    </head>
    <body>
        <p>
            <@emailMacros.msg "notification.announcement.text" />
        <p>
    </body>
    </html>
</#escape>
