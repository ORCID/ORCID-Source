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
        <p class="your-record"><@emailMacros.msg "notification.digest.record" /></p>
        <p>
            <#list digestEmail.sources as source>
                <#if source != 'ORCID'>
                    ${source}
                    <#if (digestEmail.sources?size gt 1)>
                        ,
                    </#if>
                </#if>
        </#list>
            <@emailMacros.space /><@emailMacros.msg "notification.digest.hasChanges" /></p>
        <hr style="color: #447405;border-style: solid;" />
        <p>
            <@emailMacros.msg "notification.digest.showing" /><@emailMacros.space />${emailName}<@emailMacros.space /><@emailMacros.msg "notification.digest.outOf" />${emailName}<@emailMacros.space /><@emailMacros.msg "notification.digest.changes" />
        </p>
        <#list digestEmail.notificationsBySourceId?keys?sort as sourceId>
            <#if sourceId != 'ORCID'>
                <#list digestEmail.notificationsBySourceId[sourceId].notificationsByType?keys?sort as notificationType>
                    <p><b>${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId}</b></p>
                    <#list digestEmail.notificationsBySourceId[sourceId].notificationsByType[notificationType] as notification>
                        <#if notificationType == 'PERMISSION'>
                            <p><#if notification.notificationSubject??>${notification.notificationSubject} <#if notification.createdDate??>(${notification.createdDate.year?c}-<#if notification.createdDate.month?string?length == 1>0${notification.createdDate.month?c}<#else>${notification.createdDate.month?c}</#if>-<#if notification.createdDate.day?string?length == 1>0${notification.createdDate.day?c}<#else>${notification.createdDate.day?c}</#if>)</#if><#else><@emailMacros.msg "email.digest.requesttoadd" /> <#if notification.createdDate??>(${notification.createdDate.year?c}-<#if notification.createdDate.month?string?length == 1>0${notification.createdDate.month?c}<#else>${notification.createdDate.month?c}</#if>-<#if notification.createdDate.day?string?length == 1>0${notification.createdDate.day?c}<#else>${notification.createdDate.day?c}</#if>)</#if></#if></p>
                            <br>
                            <#assign itemsByType=notification.items.itemsByType>
                            <#list itemsByType?keys?sort as itemType>
                                <b><@emailMacros.msg "email.common.recordsection." + itemType /></b> (${itemsByType[itemType]?size})
                                <br>
                                <#list itemsByType[itemType] as item>
                                    *<@emailMacros.space />${item.itemName?trim} <#if item.externalIdentifier??>(${item.externalIdentifier.type?lower_case}: ${item.externalIdentifier.value})</#if>
                                    <br>
                                </#list>
                            </#list>
                        <#elseif notificationType == 'AMENDED' && !verboseNotifications>
                            <#assign amendedSection><@emailMacros.msg "email.common.recordsection." + notification.amendedSection /></#assign>
                            <br>
                            <@emailMacros.msg "email.digest.hasupdated_1" />
                            <br>
                            ${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId}<@emailMacros.space /><@emailMacros.msg "email.digest.hasupdated_2" /><@emailMacros.space />${amendedSection?lower_case}<@emailMacros.space /><@emailMacros.msg "email.digest.hasupdated_3" /><@emailMacros.space /><#if notification.createdDate??>(${notification.createdDate.year?c}-<#if notification.createdDate.month?string?length == 1>0${notification.createdDate.month?c}<#else>${notification.createdDate.month?c}</#if>-<#if notification.createdDate.day?string?length == 1>0${notification.createdDate.day?c}<#else>${notification.createdDate.day?c}</#if>)</#if>
                        <#elseif notificationType == 'INSTITUTIONAL_CONNECTION'>
                            <@emailMacros.msg 'email.institutional_connection.1' /><@emailMacros.space />${(notification.idpName)!}<@emailMacros.space /><@emailMacros.msg 'email.institutional_connection.2' /><@emailMacros.msg 'email.institutional_connection.here' /><@emailMacros.msg 'email.institutional_connection.3' /><@emailMacros.space />${(notification.source.sourceName.content)!sourceId}<@emailMacros.space /><@emailMacros.msg 'email.institutional_connection.4' /><@emailMacros.space />${baseUri}/inbox/encrypted/${notification.encryptedPutCode}/action <#if notification.createdDate??>(${notification.createdDate.year?c}-<#if notification.createdDate.month?string?length == 1>0${notification.createdDate.month?c}<#else>${notification.createdDate.month?c}</#if>-<#if notification.createdDate.day?string?length == 1>0${notification.createdDate.day?c}<#else>${notification.createdDate.day?c}</#if>)</#if>
                            <br>
                        <#elseif notificationType != 'AMENDED'>
                            ${(notification.subject)}
                        </#if>
                    </#list>
                </#list>
            </#if>
        </#list>
        <br>
        <#if verboseNotifications>
            <#include "digest_email_amend_section.ftl"/>
        </#if>
        <#include "notification_footer_html.ftl"/>
    </div>
    </body>
    </html>
</#escape>
