<@public >
<#escape x as x?html>
<#setting date_format="yyyy-MM-dd">
<div class="row workspace-top public-profile">
    <div class="col-md-3 lhs left-aside">
        <div class="workspace-left workspace-profile">
            <div class="id-banner">
                <h2 class="full-name">                  
                    ${(displayName)!}                   
                </h2>                               
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
                        </div>                  
                    </div>
                </div>
            </div>
            <#include "/includes/ng2_templates/print-record-ng2-template.ftl">
            <print-record-ng2></print-record-ng2> 
            <!--Person sections-->
            <#include "/includes/ng2_templates/public-record-ng2-template.ftl">
            <public-record-ng2></public-record-ng2>
        </div>
    </div>
    <div class="col-md-9 right-aside">
        <div class="workspace-right">
            <div class="workspace-inner-public workspace-public workspace-accordion">
                <#if (isProfileEmpty)?? && isProfileEmpty>
                    <p class="margin-top-box"><b><@orcid.msg 'public_profile.empty_profile'/></b></p>
                <#else>             
                    <#if (biography.content)?? && (biography.content)?has_content>                                              
                        <div class="workspace-accordion-content">
                            <div class="row bottomBuffer">
                                <div class="col-md-12 col-sm-12 col-xs-12">
                                    <h3 class="workspace-title">${springMacroRequestContext.getMessage("public_profile.labelBiography")}</h3>
                                </div>
                            </div>          
                            <div class="row bottomBuffer">                  
                                <div class="col-md-12 col-sm-12 col-xs-12">
                                    <div class="bio-content">${(biography.content)!}</div>                                  
                                </div>
                            </div>                          
                        </div>
                    </#if>
                    <#assign publicProfile = true />
                    <#include "workspace_preview_activities_v3.ftl"/>
                </#if> 
                <div id="public-last-modified">
                    <p class="small italic">${springMacroRequestContext.getMessage("public_profile.labelLastModified")} ${(lastModifiedTime?datetime)!}</p>
                </div>                
            </div>
        </div>
    </div>
</div>
</#escape>
</@public>