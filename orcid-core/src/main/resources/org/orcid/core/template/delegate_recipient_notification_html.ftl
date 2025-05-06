<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
    <!DOCTYPE html>
    <html>
    <head>
        <title>${subject}</title>
    </head>
    <body>
        <p>
            <@emailMacros.msg "notification.delegate.receipt.accountDelegate" />
            <@emailMacros.space />
            <b>${emailNameGrantingPermission}</b>
            <@emailMacros.space />
            <a style="text-decoration: underline;color: #085c77;" href="${baseUri}/${grantingOrcidValue}" target="_blank">
                (${baseUri}/${grantingOrcidValue}).
            </a>
        </p>
        <p>
            <@emailMacros.msg "notification.delegate.receipt.accountDelegateMeans" />
            <@emailMacros.space />${emailNameGrantingPermissionWithApostrophe}
            <@emailMacros.msg "notification.delegate.receipt.orcidRecord" />
            <@emailMacros.space /><@emailMacros.msg "notification.delegate.receipt.revokeAnytime" /><@emailMacros.space /><a style="text-decoration: underline;color: #085c77;display: inline-block;" href="${baseUri}/trusted-parties" target="_blank"><@emailMacros.msg "notification.delegate.receipt.trustedParties" /></a><@emailMacros.space /><@emailMacros.msg "notification.delegate.receipt.sectionOf" />
        </p>
        <p>
            <@emailMacros.msg "notification.delegate.receipt.tutorial" /><@emailMacros.space />
            <a style="text-decoration: underline;color: #085c77;" href="https://support.orcid.org/hc/articles/360006973613" target="_blank">
                https://support.orcid.org/hc/articles/360006973613.
            </a>
        </p>
        <p>
            <@emailMacros.msg "notification.delegate.receipt.questions" /><@emailMacros.space />${emailNameGrantingPermission}<@emailMacros.space />
            <a style="text-decoration: underline;color: #085c77;" href="mailto:${grantingOrcidEmail}">(${grantingOrcidEmail}),</a>
            <@emailMacros.msg "notification.delegate.receipt.helpDesk" />
            <@emailMacros.space />
            <a style="text-decoration: underline;color: #085c77;" href="https://orcid.org/help/contact-us" target="_blank">                
                https://orcid.org/help/contact-us.
            </a>
        </p>
    </body>
    </html>
</#escape>
