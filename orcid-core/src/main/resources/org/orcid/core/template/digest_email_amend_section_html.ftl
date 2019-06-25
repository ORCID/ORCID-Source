<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
    <#list clientUpdates as clientUpdate>
        <p>
            <div>
                ${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId}<@emailMacros.space /><@emailMacros.msg "email.notification.client.have_name" />
            </div>
            <#if clientUpdate.haveWorks??>
                ${clientUpdate.renderWorks}
            </#if>
            <#if clientUpdate.haveEducations??>
                ${clientUpdate.renderWorks}
            </#if>
            
                
            
        </p>
    </#list>
</#escape>
