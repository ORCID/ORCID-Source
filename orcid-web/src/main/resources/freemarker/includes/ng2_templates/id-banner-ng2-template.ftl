<script type="text/ng-template" id="id-banner-ng2-template">

    <div [ngClass]="(personService.userInfo['IN_DELEGATION_MODE'] == 'true') ? 'id-banner delegation-mode' : 'id-banner'"> 
        
        <span *ngIf="personService.userInfo['IN_DELEGATION_MODE'] == 'true'" class="delegation-mode-warning"><@orcid.msg 'delegate.managing_record'/></span>
        <!-- Name -->

        <#if springMacroRequestContext.requestUri?contains("/my-orcid")>
        <name-ng2 class="clearfix"></name-ng2>  
        </#if>

        <#if !springMacroRequestContext.requestUri?contains("/my-orcid")>
        <h2 class="full-name">                  
            {{displayName}}              
        </h2>  
        </#if>  

        <div class="oid">
            <div class="id-banner-header">
                <span><@orcid.msg 'common.orcid_id' /></span>
            </div>
            <div class="orcid-id-container">
                <div class="orcid-id-options">
                    <div class="orcid-id-info">
                        <span class="mini-orcid-icon-16"></span>
                        <span id="orcid-id" class="orcid-id-https">${baseUri}/{{personService.userInfo['EFFECTIVE_USER_ORCID']}}</span>
                    </div>
                    <#if springMacroRequestContext.requestUri?contains("/my-orcid")>
                    <a href="${baseUri}/{{personService.userInfo['EFFECTIVE_USER_ORCID']}}" class="gray-button" target="id_banner.viewpublicprofile"><@orcid.msg 'id_banner.viewpublicprofile'/></a>
                    </#if> 
                </div>
            </div>
        </div>
            <switch-user-ng2></switch-user-ng2> 
    </div>
</script>
<#include "/includes/ng2_templates/name-ng2-template.ftl">
<#include "/includes/ng2_templates/switch-user-ng2-template.ftl">