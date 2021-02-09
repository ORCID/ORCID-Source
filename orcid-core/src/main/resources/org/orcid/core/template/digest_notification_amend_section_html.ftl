<#import "email_macros.ftl" as emailMacros />
<#assign types = ["BIO", "DISTINCTION", "EDUCATION", "EMPLOYMENT", "EXTERNAL_IDENTIFIER", "INVITED_POSITION", "FUNDING", "MEMBERSHIP", "PEER_REVIEW", "QUALIFICATION", "SERVICE", "WORK", "RESEARCH_RESOURCE"]>
<#assign actions = ["CREATE", "DELETE", "UPDATE", "UNKNOWN"]>
<#list clientUpdates as clientUpdate>
    <div>
        <p>
            <#if (maxPerClient < clientUpdate.counter) >
                <i><@emailMacros.msg "notification.digest.showing" /></i><@emailMacros.space />
                <strong>${maxPerClient}</strong><@emailMacros.space />
                <i><@emailMacros.msg "notification.digest.outOf" /></i><@emailMacros.space />
                <strong>${clientUpdate.counter}</strong><@emailMacros.space />
                <i><@emailMacros.msg "notification.digest.changes" /></i>
            <#else>
                <i><@emailMacros.msg "notification.digest.showing" /></i><@emailMacros.space />
                <strong>${clientUpdate.counter}</strong><@emailMacros.space />
                <i><@emailMacros.msg "notification.digest.outOf" /></i><@emailMacros.space />
                <strong>${clientUpdate.counter}</strong><@emailMacros.space />
                <i><@emailMacros.msg "notification.digest.changes" /></i>
            </#if>
        </p>
    </div>
    <#list types as type>
        <#if clientUpdate.updates[type]??>
            <div>
                <p>
                    <strong style="text-transform: uppercase;"><@emailMacros.msg "email.common.recordsection." + type /></strong>
                </p>
                <#assign actionsPerType = clientUpdate.updates[type]>
                <#list actions as action>
                    <#if actionsPerType[action]??>
                        <#assign elements = actionsPerType[action]>
                        <#list elements>
                            <div>
                                <p style="margin-bottom: 2px;">
                                    <strong><@emailMacros.msg "notification.digest." + action /></strong></p>
                            </div>
                            <div>
                                <ul style="padding-left: 0;margin-top: 2px;">
                                    <#items as itemName>
                                        <li>${itemName}</li>
                                    </#items>
                                </ul>
                            </div>
                        </#list>
                    </#if>
                </#list>
            </div>
            <br>
        </#if>
    </#list>
</#list>
