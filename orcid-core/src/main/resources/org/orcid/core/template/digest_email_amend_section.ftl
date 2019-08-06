<#import "email_macros.ftl" as emailMacros />
<#assign types = ["BIO", "DISTINCTION", "EDUCATION", "EMPLOYMENT", "EXTERNAL_IDENTIFIER", "INVITED_POSITION", "FUNDING", "MEMBERSHIP", "PEER_REVIEW", "QUALIFICATION", "SERVICE", "WORK", "RESEARCH_RESOURCE"]>
<#assign actions = ["CREATE", "DELETE", "UPDATE", "UNKNOWN"]>
<#list clientUpdates as clientUpdate>
    ${(clientUpdate.clientName)!clientUpdate.clientId}<@emailMacros.space /><@emailMacros.msg "email.notification.client.has_made" />
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