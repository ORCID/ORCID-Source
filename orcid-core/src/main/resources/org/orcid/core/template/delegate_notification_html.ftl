<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<!DOCTYPE html>
<html>
    <head>
        <title>${subject}</title>
    </head>
    <body>
            <p>
                <@emailMacros.msg "notification.delegate.added" /><@emailMacros.space />${emailNameForDelegate}<@emailMacros.space />
            <a style="text-decoration: underline;color: #085c77;display: inline-block;" href="${baseUri}/${orcidValueForDelegate}" target="_blank">
                (${baseUri}/${orcidValueForDelegate})
            </a>
                <@emailMacros.space />
                <@emailMacros.msg "notification.delegate.asTrustedIndividual" />
            <p>
                <@emailMacros.msg "notification.delegate.accessYourRecord" /><@emailMacros.space />
            <a style="text-decoration: underline;color: #085c77;" href="${baseUri}/account" target="_blank">
                <@emailMacros.msg "notification.delegate.settings" />
            </a>
            </p>
        </body>
 </html>
 </#escape>
