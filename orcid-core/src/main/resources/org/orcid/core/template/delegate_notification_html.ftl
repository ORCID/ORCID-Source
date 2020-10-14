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
            <hr style="color: #447405;border-style: solid;" />
            <p style="font-size: 12px;font-weight: 600;color: #447405;">
                <@emailMacros.msg "notification.delegate.record" />
            </p>
            <p>${emailNameForDelegate}<@emailMacros.space /><@emailMacros.msg "notification.delegate.trustedIndividual" /></p>
            <hr style="color: #447405;border-style: solid;" />
            <p>
                <@emailMacros.msg "notification.delegate.added" /><@emailMacros.space />${emailNameForDelegate}<@emailMacros.space />
            <a style="text-decoration: underline;color: #085c77;" href="${baseUri}/${grantingOrcidValue}">
                (${baseUri}/${grantingOrcidValue})
            </a>
                <@emailMacros.msg "notification.delegate.asTrustedIndividual" />
            <p>
                <@emailMacros.msg "notification.delegate.accessYourRecord" /><@emailMacros.space />
            <a style="text-decoration: underline;color: #085c77;" href="${baseUri}/account">
                <@emailMacros.msg "notification.delegate.settings" />
            </a>
            </p>
            <#include "notification_footer_html.ftl"/>
            </div>
        </body>
 </html>
 </#escape>
