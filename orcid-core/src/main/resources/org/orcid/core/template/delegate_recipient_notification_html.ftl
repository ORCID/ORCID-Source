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
        <p class="your-record"><@emailMacros.msg "notification.delegate.receipt.record" /></p>
        <p>${emailNameGrantingPermission}<@emailMacros.space /><@emailMacros.msg "notification.delegate.receipt.trustedIndividual" /></p>
        <hr class="title" />
        <p>
            <@emailMacros.msg "notification.delegate.receipt.accountDelegate" /><@emailMacros.space />${emailNameGrantingPermission}<@emailMacros.space /><@emailMacros.msg "notification.delegate.asTrustedIndividual" />
        <p>
            <@emailMacros.msg "notification.delegate.receipt.accountDelegateMeans" />
            <@emailMacros.space />${emailNameGrantingPermission}<@emailMacros.space />
            <@emailMacros.msg "notification.delegate.receipt.orcidRecord" />
        </p>
        <p>
            <@emailMacros.msg "notification.delegate.receipt.tutorial" /><@emailMacros.space />
            <a href="https://support.orcid.org/hc/articles/360006973613"><@emailMacros.msg "notification.delegate.receipt.tutorialLink" /></a>
        </p>
        <p>
            <@emailMacros.msg "notification.delegate.receipt.questions" /><@emailMacros.space />${emailNameGrantingPermission}<@emailMacros.msg "notification.delegate.receipt.helpDesk" /><@emailMacros.space />
            <a href="https://orcid.org/help/contact-us"><@emailMacros.msg "notification.delegate.receipt.helpDeskLink" /></a>
        </p>
        <#include "notification_footer_html.ftl"/>
    </div>
    </body>
    </html>
</#escape>
