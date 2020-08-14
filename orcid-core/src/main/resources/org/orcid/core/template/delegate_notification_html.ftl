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
            <p class="your-record"><@emailMacros.msg "notification.delegate.record" /></p>
            <p>${emailNameForDelegate}<@emailMacros.space /><@emailMacros.msg "notification.delegate.trustedIndividual" /></p>
            <hr class="title" />
            <p>
                <@emailMacros.msg "notification.delegate.added" /><@emailMacros.space />${emailNameForDelegate}<@emailMacros.space />
            <a href="#">(${baseUri}/${grantingOrcidValue}) </a>
                <@emailMacros.msg "notification.delegate.asTrustedIndividual" />
            <p>
                <@emailMacros.msg "notification.delegate.accessYourRecord" /><@emailMacros.space />
            <a href="#"><@emailMacros.msg "notification.delegate.settings" /></a>
            </p>
            <#include "notification_footer_html.ftl"/>
            </div>
        </body>
 </html>
 </#escape>
