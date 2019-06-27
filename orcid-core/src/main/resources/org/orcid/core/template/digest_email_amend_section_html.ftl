<#import "email_macros.ftl" as emailMacros />
<#assign types = ["BIO", "DISTINCTION", "EDUCATION", "EMPLOYMENT", "EXTERNAL_IDENTIFIER", "INVITED_POSITION", "FUNDING", "MEMBERSHIP", "PEER_REVIEW", "QUALIFICATION", "SERVICE", "WORK", "RESEARCH_RESOURCE"]>
<#assign actions = ["CREATE", "DELETE", "UPDATE", "UNKNOWN"]>
<#list clientUpdates as clientUpdate>
    <p>
        <div> 
            <strong>${(clientUpdate.clientName)!clientUpdate.clientId}</strong><@emailMacros.space /><@emailMacros.msg "email.notification.client.have_made" />
        </div> 
        <div style="padding-left:25px">
            <#list types as type>
                <#if clientUpdate.updates[type]??>
                    <div>
                        <p><strong><@emailMacros.msg "email.common.recordsection." + type /></strong></p>
                        <#assign actionsPerType = clientUpdate.updates[type]>
                        <#list actions as action>
                            <#if actionsPerType[action]??>
                                <#assign elements = actionsPerType[action]>
                                <#list elements>
                                    <div>
                                        <p><@emailMacros.msg "email.notification.client." + action /></p>
                                    </div>
                                    <div>
                                        <ul>
                                            <#items as itemName>
                                            <li>${itemName}</li>
                                            </#items>
                                        </ul>                
                                    </div>
                                </#list>
                            </#if>
                        </#list>                
                    </div>    
                </#if>
            </#list>
            <#if (maxPerClient < clientUpdate.counter) >
                <div>
                    <p>
                        <strong><@emailMacros.msg "email.notification.client.showing.1" /><@emailMacros.space />${maxPerClient}<@emailMacros.space /><@emailMacros.msg "email.notification.client.showing.2" /><@emailMacros.space />${clientUpdate.counter}<@emailMacros.space /><@emailMacros.msg "email.notification.client.showing.3" /></strong>
                    </p>
                </div>
            </#if>
        </div>
    </p>        
</#list>
