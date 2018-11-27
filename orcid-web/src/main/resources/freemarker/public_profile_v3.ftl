<@public >
<#escape x as x?html>
<#setting date_format="yyyy-MM-dd">
<div class="row  public-profile">
    <div class="col-md-3 lhs left-aside">
        <div class="workspace-left workspace-profile">
            <#include "/includes/ng2_templates/id-banner-ng2-template.ftl"/>
            <id-banner-ng2> </id-banner-ng2>
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
                <#if (peerReviewEmpty)?? && (affiliationsEmpty)?? && (fundingEmpty)?? && (researchResourcesEmpty)?? && (worksEmpty)?? && (biography.content)?? && (biography.content)?has_content>
                    <p class="margin-top-box"><b><@orcid.msg 'public_profile.empty_profile'/></b></p>
                <#else>                                             
                    <#include "/includes/ng2_templates/bio-ng2-template.ftl">
                    <bio-ng2></bio-ng2>
                    <#assign publicProfile = true />
                    <#if !(affiliationsEmpty)??>
                        <#include "/includes/ng2_templates/affiliation-ng2-template.ftl">
                        <affiliation-ng2  publicView="true"></affiliation-ng2>
                    </#if>
                    <!-- Funding -->
                    <#if !(fundingEmpty)??>     
                        <#include "/includes/ng2_templates/funding-ng2-template.ftl">
                        <funding-ng2  publicView="true"></funding-ng2>
                    </#if>
                    <@orcid.checkFeatureStatus 'RESEARCH_RESOURCE'>
                    <#if !(researchResourcesEmpty)??>  
                        <!-- Research resources -->
                        <#include "/includes/ng2_templates/research-resource-ng2-template.ftl">
                        <research-resource-ng2  publicView="true"></research-resource-ng2>
                    </#if>
                    </@orcid.checkFeatureStatus>
                    <!-- Works -->
                    <#if !(worksEmpty)??> 
                    <#include "/includes/ng2_templates/works-ng2-template.ftl">
                    <works-ng2  publicView="true"></works-ng2>
                    </#if>
                    <!-- Peer Review -->
                    <#if !(peerReviewEmpty)??> 
                    <#include "/includes/ng2_templates/peer-review-ng2-template.ftl">
                    <peer-review-ng2 publicView="true"></peer-review-ng2>
                    </#if>
                </#if> 
                <#include "/includes/ng2_templates/last-modified-ng2-template.ftl">
                <last-modified-ng2> </last-modified-ng2>
            </div>
        </div>
    </div>
</div>
</#escape>
<!--Org ID popover template used in v3 affiliations and research resources-->
<#include "/includes/ng2_templates/org-identifier-popover-ng2-template.ftl">
</@public>