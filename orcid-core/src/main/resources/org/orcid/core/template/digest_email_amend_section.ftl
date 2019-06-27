<#import "email_macros.ftl" as emailMacros />
<#if verboseNotifications??>
    <#assign types = ["BIO", "DISTINCTION", "EDUCATION", "EMPLOYMENT", "EXTERNAL_IDENTIFIER", "INVITED_POSITION", "FUNDING", "MEMBERSHIP", "PEER_REVIEW", "QUALIFICATION", "SERVICE", "WORK", "RESEARCH_RESOURCE"]>
    <#assign actions = ["CREATE", "DELETE", "UPDATE", "UNKNOWN"]>
    <#list clientUpdates as clientUpdate>
        ${(clientUpdate.clientName)!clientUpdate.clientId}<@emailMacros.space /><@emailMacros.msg "email.notification.client.have_made" />
        <#list types as type>
            <#if clientUpdate.updates[type]??>
                <@emailMacros.msg "email.common.recordsection." + type />
                <#assign actionsPerType = clientUpdate.updates[type]>
                <#list actions as action>
                    <#if actionsPerType[action]??>
                        <#assign elements = actionsPerType[action]>
                        <#list elements>
                            <@emailMacros.msg "email.notification.client." + action />
                            <#items as itemName>
                                * ${itemName}
                            </#items>
    
                        </#list>
                    </#if>
                </#list>                
            </#if>
        </#list>                
    </#list>
<#else>
    <#assign amendedSection><@emailMacros.msg "email.common.recordsection." + notification.amendedSection /></#assign>
    <@emailMacros.msg "email.digest.hasupdated_1" />
        ${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId}<@emailMacros.space /><@emailMacros.msg "email.digest.hasupdated_2" /><@emailMacros.space />${amendedSection?lower_case}<@emailMacros.space /><@emailMacros.msg "email.digest.hasupdated_3" /><#if notification.createdDate??>(${notification.createdDate.year?c}-<#if notification.createdDate.month?string?length == 1>0${notification.createdDate.month?c}<#else>${notification.createdDate.month?c}</#if>-<#if notification.createdDate.day?string?length == 1>0${notification.createdDate.day?c}<#else>${notification.createdDate.day?c}</#if>)</#if>
    <#if notification.items??>
        <#list notification.items.items as item>
            *${item.itemName} <#if item.externalIdentifier??>(${item.externalIdentifier.type?lower_case}: ${item.externalIdentifier.value})</#if>
        </#list>        
    </#if>
</#if>