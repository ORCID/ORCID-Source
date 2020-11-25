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
            <@emailMacros.space />${emailNameGrantingPermission}<@emailMacros.space />
            <a style="text-decoration: underline;color: #085c77;" href="${baseUri}/${grantingOrcidValue}" target="_blank">
                (${baseUri}/${grantingOrcidValue})
            </a>
        <p>
            <@emailMacros.msg "notification.delegate.receipt.accountDelegateMeans" />
            <@emailMacros.space />${emailNameGrantingPermission}
            <span style="margin-left: 3px">
                <@emailMacros.msg "notification.delegate.receipt.orcidRecord" />
            </span>
        </p>
        <p>
            <@emailMacros.msg "notification.delegate.receipt.tutorial" /><@emailMacros.space />
            <a style="text-decoration: underline;color: #085c77;" href="https://support.orcid.org/hc/articles/360006973613" target="_blank">
                <@emailMacros.msg "notification.delegate.receipt.tutorialLink" />
            </a>
        </p>
        <p>
            <@emailMacros.msg "notification.delegate.receipt.questions" /><@emailMacros.space />${emailNameGrantingPermission}<@emailMacros.space />
            <a style="text-decoration: underline;color: #085c77;" href="mailto:${grantingOrcidEmail}">(${grantingOrcidEmail})</a>
            <span style="margin-left: 3px">
                <@emailMacros.msg "notification.delegate.receipt.helpDesk" />
            </span>
            <@emailMacros.space />            
            <a style="text-decoration: underline;color: #085c77;" href="https://orcid.org/help/contact-us" target="_blank">                
                <@emailMacros.msg "notification.delegate.receipt.helpDeskLink" />
            </a>
        </p>
    </body>
    </html>
</#escape>
