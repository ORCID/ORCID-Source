<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
    <#list clientUpdates as clientUpdate>
        <p>
            <div>
                ${(digestEmail.notificationsBySourceId[sourceId].source.sourceName.content)!sourceId}<@emailMacros.space /><@emailMacros.msg "email.notification.client.have_name" />
            </div>
            ${clientUpdate.renderBio}
            ${clientUpdate.renderDistinction}
            ${clientUpdate.renderWorks}
            ${clientUpdate.renderEmployment}
            ${clientUpdate.renderExternalIdentifier}
            ${clientUpdate.renderInvitedPosition}
            ${clientUpdate.renderFunding}
            ${clientUpdate.renderMembership}
            ${clientUpdate.renderPeerReview}
            ${clientUpdate.renderQualification}
            ${clientUpdate.renderService}
            ${clientUpdate.renderWorks}
            ${clientUpdate.renderResearchResources}            
        </p>
    </#list>
</#escape>
