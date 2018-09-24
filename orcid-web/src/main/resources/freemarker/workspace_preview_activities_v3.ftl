<#if !(affiliationsEmpty)??>

    <@orcid.checkFeatureStatus 'DISPLAY_NEW_AFFILIATION_TYPES'> 
        <affiliation-ng2  publicView="true"></affiliation-ng2>
    </@orcid.checkFeatureStatus>

    <@orcid.checkFeatureStatus 'DISPLAY_NEW_AFFILIATION_TYPES' false>
        <!-- Education -->
        <div id="workspace-education" class="workspace-accordion-item workspace-accordion-active" ng-controller="PublicEduAffiliation" ng-hide="!affiliationsSrvc.educations.length" ng-cloak>        
        
            <#include "includes/affiliate/edu_section_header_inc.ftl" />
            <div ng-if="workspaceSrvc.displayEducation" class="workspace-accordion-content">
                <#include "includes/affiliate/edu_body_inc.ftl" />
            </div>           
        </div>
        <!-- Employment -->
        <div id="workspace-employment" class="workspace-accordion-item workspace-accordion-active" ng-controller="PublicEmpAffiliation" ng-hide="!affiliationsSrvc.employments.length" ng-cloak>
            <#include "includes/affiliate/emp_section_header_inc.ftl" />
            <div ng-if="workspaceSrvc.displayEmployment" class="workspace-accordion-content">
                <#include "includes/affiliate/emp_body_inc.ftl" />  
            </div>
        </div>
    </@orcid.checkFeatureStatus>
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
<!-- Works -->
<#if !(peerReviewEmpty)??> 
<#include "/includes/ng2_templates/peer-review-ng2-template.ftl">
<peer-review-ng2 publicView="true"></peer-review-ng2>
</#if>
    
    