<#import "email_macros.ftl" as emailMacros />

<#include "notification_header.ftl"/>
${'\n'}<@emailMacros.msg "notification.share.record" />
${'\n'}${emailName}<@emailMacros.space /><@emailMacros.msg "notification.digest.hasChanges" />

<#if digestEmail.notificationsBySourceId['ORCID']??>
    <@emailMacros.msg "email.digest.orcidwouldlikeyoutoknow" />


    <#list digestEmail.notificationsBySourceId['ORCID'].allNotifications as notification>
        ${notification.subject}
    </#list>

</#if>
<#list digestEmail.notificationsBySourceId?keys?sort as sourceId>
    <#if sourceId != 'ORCID'>
        <#list digestEmail.notificationsBySourceId[sourceId].notificationsByType?keys?sort as notificationType>
            <#if sourceId == 'AMENDED' && !verboseNotifications>
                ${'\n'}<@emailMacros.msg "notification.digest.showing" /><@emailMacros.space />${digestEmail.sources?size}<@emailMacros.space /><@emailMacros.msg "notification.digest.outOf" /><@emailMacros.space />${digestEmail.sources?size}<@emailMacros.space /><@emailMacros.msg "notification.digest.changes" />
            </#if>
            <#list digestEmail.notificationsBySourceId[sourceId].notificationsByType[notificationType] as notification>

                <#if notificationType == 'PERMISSION'>
                    <#if notification.notificationIntro?? && notification.notificationIntro?contains("::")>
                            <#assign splitValues = notification.notificationIntro?split("::") />
    						<#assign memberName = splitValues[0] />
    						<#assign memberWebUrl = splitValues[1] />
    						${notification.notificationSubject}
    						
                            <@emailMacros.msg "notification.mvp.youCanBenefit" />
                            <@emailMacros.msg "notification.mvp.basedOnYourVerifiedEmail" /><@emailMacros.space />${memberName}.<@emailMacros.space /><@emailMacros.msg "notification.mvp.connectingWithThisIntegration" /><@emailMacros.space />${memberName}<@emailMacros.space /><@emailMacros.msg "notification.mvp.toAutomaticallyAdd" />
					        <@emailMacros.msg "notification.mvp.connectWith" /><@emailMacros.space />${memberName}: <@emailMacros.space /> ${memberWebUrl}
					<#else>	
					<#if notification.notificationIntro??>${notification.notificationIntro}</#if>
                    ${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId}: <#if notification.notificationSubject??>${notification.notificationSubject} <#if notification.createdDate??>(${notification.createdDate.year?c}-<#if notification.createdDate.month?string?length == 1>0${notification.createdDate.month?c}<#else>${notification.createdDate.month?c}</#if>-<#if notification.createdDate.day?string?length == 1>0${notification.createdDate.day?c}<#else>${notification.createdDate.day?c}</#if>)</#if><#else><@emailMacros.msg "email.digest.requesttoadd" /> <#if notification.createdDate??>(${notification.createdDate.year?c}-<#if notification.createdDate.month?string?length == 1>0${notification.createdDate.month?c}<#else>${notification.createdDate.month?c}</#if>-<#if notification.createdDate.day?string?length == 1>0${notification.createdDate.day?c}<#else>${notification.createdDate.day?c}</#if>)</#if></#if>
                    <#assign itemsByType=notification.items.itemsByType>
                    <#list itemsByType?keys?sort as itemType>
                        <@emailMacros.msg "email.common.recordsection." + itemType /> (${itemsByType[itemType]?size})
                        <#list itemsByType[itemType] as item>
                            ${item.itemName?trim} <#if item.externalIdentifier??>(${item.externalIdentifier.type?lower_case}: ${item.externalIdentifier.value})</#if>
                        </#list>

                        <@emailMacros.msg "email.digest.plaintext.addnow" /><@emailMacros.space />${baseUri}/inbox/encrypted/${notification.encryptedPutCode}/action
                        <@emailMacros.msg "email.digest.plaintext.moreinfo" /><@emailMacros.space />${baseUri}/inbox#${notification.putCode}
                    </#list>
                    </#if>
                <#elseif notificationType == 'AMENDED' && !verboseNotifications>
                    <#assign amendedSection><@emailMacros.msg "email.common.recordsection." + notification.amendedSection /></#assign>
                    <@emailMacros.msg "email.digest.hasupdated_1" />
                    ${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId}<@emailMacros.space /><@emailMacros.msg "email.digest.hasupdated_2" /><@emailMacros.space />${amendedSection?lower_case}<@emailMacros.space /><@emailMacros.msg "email.digest.hasupdated_3" /><@emailMacros.space /><#if notification.createdDate??>(${notification.createdDate.year?c}-<#if notification.createdDate.month?string?length == 1>0${notification.createdDate.month?c}<#else>${notification.createdDate.month?c}</#if>-<#if notification.createdDate.day?string?length == 1>0${notification.createdDate.day?c}<#else>${notification.createdDate.day?c}</#if>)</#if>
                <#elseif notificationType == 'INSTITUTIONAL_CONNECTION'>
                    <@emailMacros.msg 'email.institutional_connection.1' /><@emailMacros.space />${(notification.idpName)!}<@emailMacros.space /><@emailMacros.msg 'email.institutional_connection.2' /><@emailMacros.msg 'email.institutional_connection.here' /><@emailMacros.msg 'email.institutional_connection.3' /><@emailMacros.space />${(notification.source.sourceName.content)!sourceId}<@emailMacros.space /><@emailMacros.msg 'email.institutional_connection.4' /><@emailMacros.space />${baseUri}/inbox/encrypted/${notification.encryptedPutCode}/action <#if notification.createdDate??>(${notification.createdDate.year?c}-<#if notification.createdDate.month?string?length == 1>0${notification.createdDate.month?c}<#else>${notification.createdDate.month?c}</#if>-<#if notification.createdDate.day?string?length == 1>0${notification.createdDate.day?c}<#else>${notification.createdDate.day?c}</#if>)</#if>
                <#elseif notificationType != 'AMENDED'>
                    ${(notification.subject)}
                </#if>
            </#list>
        </#list>
    </#if>
</#list>
<#if verboseNotifications>
    <#include "digest_notification_amend_section.ftl"/>
</#if>

<#include "notification_footer.ftl"/>
