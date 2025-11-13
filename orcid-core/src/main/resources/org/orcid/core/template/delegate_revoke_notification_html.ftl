<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<!DOCTYPE html>
<html>
    <head>
        <title>${subject}</title>
    </head>
    <body>
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
            <p>
                <@emailMacros.msg "notification.delegate.receipt.revokeConcerns" /><@emailMacros.space />
            <@emailMacros.space /><a style="text-decoration: underline;color: #085c77;display: inline-block;" href="https://orcid.org/help/contact-us" target="_blank">
                https://orcid.org/help/contact-us.
            </a>
            </p>
            
        </body>
 </html>
 </#escape>
