<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<!DOCTYPE html>
<html>
    <head>
        <title>${subject}</title>
    </head>
    <body>
    <#include "notification_header_html.ftl"/>
            <p>
                <b>${emailNameForDelegate}</b>
                <@emailMacros.space />
            <a style="text-decoration: underline;color: #085c77;display: inline-block;" href="${baseUri}/${orcidValueForDelegate}" target="_blank">
                (${baseUri}/${orcidValueForDelegate})
            </a>
                <@emailMacros.space />
                <@emailMacros.msg "notification.delegate.receipt.revokeTrustedIndividual" />
            <p>
                <@emailMacros.msg "notification.delegate.receipt.revokeAccessYourRecord" /><@emailMacros.space />
            <@emailMacros.space /><a style="text-decoration: underline;color: #085c77;display: inline-block;" href="${baseUri}/trusted-parties" target="_blank">
                <@emailMacros.msg "notification.delegate.receipt.trustedIndividuals" />
            </a>
            </p>
            <#include "notification_footer_html.ftl"/>
        </body>
 </html>
 </#escape>
