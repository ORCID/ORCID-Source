<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
    <!DOCTYPE html>
    <html>
    <head>
        <title>${subject}</title>
    </head>
    <body>
        <p>
            <@emailMacros.msg "notification.share.announcement.text" />
        <p>
    </body>
    </html>
</#escape>
