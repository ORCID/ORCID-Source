<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
    <!DOCTYPE html>
    <html>
    <head>
        <title>
            <@emailMacros.msg "notification.admin_delegate_request.title.1" /><@emailMacros.space />${trustedOrcidName}<@emailMacros.space /><@emailMacros.msg "notification.admin_delegate_request.title.2" />
        </title>
    </head>
    <body>
    <p>
        <@emailMacros.msg "notification.admin_delegate_request.you_have.1" /><@emailMacros.space />${trustedOrcidName}
        <@emailMacros.space /><@emailMacros.msg "notification.admin_delegate_request.you_have.2" />
            <a style="text-decoration: underline;color: #085c77;" href="${baseUri}/${trustedOrcidValue}">${baseUri}/${trustedOrcidValue}</a>
        <span style="margin-left: -3px">
            )
        </span>
        <@emailMacros.space /><@emailMacros.msg "notification.admin_delegate_request.you_have.3" />
        <a style="text-decoration: underline;color: #085c77;" href="${baseUri}/${managedOrcidValue}">${baseUri}/${managedOrcidValue}</a>
        <span style="margin-left: -3px">
            .
        </span>
    </p>
    <p>
        <@emailMacros.space /><@emailMacros.msg "notification.admin_delegate_request.you_have.4" />
    </p>
    <p>
        <@emailMacros.msg "notification.admin_delegate_request.you_have.5" /><br/>
        <a style="text-decoration: underline;color: #085c77;" href="${link}" rel="noopener noreferrer" target="orcid.blank">${link}</a>
    </p>
    <p>
        <@emailMacros.msg "notification.admin_delegate_request.for_a_tutorial" />
        <a style="text-decoration: underline;color: #085c77;" href="https://support.orcid.org/hc/articles/360006973613" rel="noopener noreferrer" target="orcid.blank">https://support.orcid.org/hc/articles/360006973613</a>
    </p>
    </body>
    </html>
</#escape>
