<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
    <#if verboseNotifications??>
        <#list clientUpdates as clientUpdate>
            <p>
                <div>
                    ${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId}<@emailMacros.space /><@emailMacros.msg "email.notification.client.have_name" />
                </div>
                    <p>${clientUpdate.clientName}</p>
                ${clientUpdate.renderBio()}
                ${clientUpdate.renderDistinction()}
                ${clientUpdate.renderWorks()}
                ${clientUpdate.renderEmployment()}
                ${clientUpdate.renderExternalIdentifier()}
                ${clientUpdate.renderInvitedPosition()}
                ${clientUpdate.renderFunding()}
                ${clientUpdate.renderMembership()}
                ${clientUpdate.renderPeerReview()}
                ${clientUpdate.renderQualification()}
                ${clientUpdate.renderService()}
                ${clientUpdate.renderWorks()}
                ${clientUpdate.renderResearchResources()}            
            </p>
        </#list>
    <#else>
        <#assign amendedSection><@emailMacros.msg "email.common.recordsection." + notification.amendedSection /></#assign>
        <div>
            <img src="https://orcid.org/sites/all/themes/orcid/img/update.png">&nbsp;<@emailMacros.msg "email.digest.hasupdated_1" />
            ${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId}<@emailMacros.space /><@emailMacros.msg "email.digest.hasupdated_2" /><@emailMacros.space />${amendedSection?lower_case}<@emailMacros.space /><@emailMacros.msg "email.digest.hasupdated_3" /><#if notification.createdDate??>(${notification.createdDate.year?c}-<#if notification.createdDate.month?string?length == 1>0${notification.createdDate.month?c}<#else>${notification.createdDate.month?c}</#if>-<#if notification.createdDate.day?string?length == 1>0${notification.createdDate.day?c}<#else>${notification.createdDate.day?c}</#if>)</#if>
        </div>
        <#if notification.items??>
            <ul>
            <#list notification.items.items as item>
                <li>${item.itemName} <#if item.externalIdentifier??>(${item.externalIdentifier.type?lower_case}: ${item.externalIdentifier.value})</#if></li>
            </#list>
            </ul>
        </#if>
    </#if>
</#escape>
