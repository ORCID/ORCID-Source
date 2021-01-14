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
            <span style="margin-right: -3px">
                <@emailMacros.space />${emailNameGrantingPermissionWithApostrophe}
            </span>
            <span style="margin-left: -3px">
                <@emailMacros.msg "notification.delegate.receipt.orcidRecord" />
            </span>
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
