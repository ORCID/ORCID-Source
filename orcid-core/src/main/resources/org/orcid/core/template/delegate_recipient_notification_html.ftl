<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
    <!DOCTYPE html>
    <html>
    <head>
        <title>${subject}</title>
    </head>
    <body>
    <div style="
                max-width: 736px;
                padding: 32px;
                margin: auto;
                font-family: Arial, helvetica, sans-serif;
                color: #494A4C;
                font-size: 15px;
            ">
        <#include "notification_header_html.ftl"/>
        <hr style="color: #447405;border-style: solid;border-width: 2px;" />
        <p style="font-size: 12px;font-weight: 600;color: #447405;">
            <@emailMacros.msg "notification.delegate.receipt.record" />
        </p>
        <p>${emailNameGrantingPermission}<@emailMacros.space /><@emailMacros.msg "notification.delegate.receipt.trustedIndividual" /></p>
        <hr style="color: #447405;border-style: solid;border-width: 2px;" />
        <p>
            <@emailMacros.msg "notification.delegate.receipt.accountDelegate" /><@emailMacros.space />${emailNameGrantingPermission}<@emailMacros.space /><@emailMacros.msg "notification.delegate.asTrustedIndividual" />
        <p>
            <@emailMacros.msg "notification.delegate.receipt.accountDelegateMeans" />
            <@emailMacros.space />${emailNameGrantingPermission}<@emailMacros.space />
            <@emailMacros.msg "notification.delegate.receipt.orcidRecord" />
        </p>
        <p>
            <@emailMacros.msg "notification.delegate.receipt.tutorial" /><@emailMacros.space />
            <a style="text-decoration: underline;color: #085c77;" href="https://support.orcid.org/hc/articles/360006973613">
                <@emailMacros.msg "notification.delegate.receipt.tutorialLink" />
            </a>
        </p>
        <p>
            <@emailMacros.msg "notification.delegate.receipt.questions" /><@emailMacros.space />${emailNameGrantingPermission}<@emailMacros.msg "notification.delegate.receipt.helpDesk" /><@emailMacros.space />
            <a style="text-decoration: underline;color: #085c77;" href="https://orcid.org/help/contact-us">
                <@emailMacros.msg "notification.delegate.receipt.helpDeskLink" />
            </a>
        </p>
        <#include "notification_footer_html.ftl"/>
    </div>
    </body>
    </html>
</#escape>
