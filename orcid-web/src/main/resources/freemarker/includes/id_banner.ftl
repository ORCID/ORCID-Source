<#escape x as x?html>

<div class="id-banner <#if inDelegationMode>delegation-mode</#if>"> 
    
    <#if inDelegationMode><span class="delegation-mode-warning">${springMacroRequestContext.getMessage("delegate.managing_record")}</span></#if>
    
    <!-- Name -->
    <#include "/includes/ng2_templates/name-ng2-template.ftl">
    <name-ng2 class="clearfix"></name-ng2>

    <div class="oid">
        <div class="id-banner-header">
            <span><@orcid.msg 'common.orcid_id' /></span>
        </div>
        <div class="orcid-id-container">
            <div class="orcid-id-options">
                <div class="orcid-id-info">
                    <span class="mini-orcid-icon-16"></span>
                    <!-- Reference: orcid.js:removeProtocolString() -->
                    <span id="orcid-id" class="orcid-id-https">${baseUri}/${(effectiveUserOrcid)!}</span>
                </div>
                <a href="${baseUri}/${(effectiveUserOrcid)!}" class="gray-button" target="id_banner.viewpublicprofile"><@orcid.msg 'id_banner.viewpublicprofile'/></a>
            </div>
        </div>
    </div>
    <#if (locked)?? && !locked>
        <#include "/includes/ng2_templates/switch-user-ng2-template.ftl">
        <switch-user-ng2></switch-user-ng2> 
    </#if>
</div>
</#escape>
