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
    <div id="workspace-funding" class="workspace-accordion-item workspace-accordion-active" ng-controller="PublicFundingCtrl" ng-cloak>
        <#include "includes/funding/funding_section_header_inc_v3.ftl" />
        <div ng-if="workspaceSrvc.displayFunding" class="workspace-accordion-content">
            <#include "includes/funding/body_funding_inc_v3.ftl" /> 
        </div>
    </div>
</#if>

<!-- Works -->
<#include "/includes/ng2_templates/works-ng2-template.ftl">
<works-ng2  publicView="true"></works-ng2>

<!-- Peer Review -->
<div id="workspace-peer-review" class="workspace-accordion-item workspace-accordion-active" ng-controller="PublicPeerReviewCtrl" ng-hide="!peerReviewSrvc.groups.length" ng-cloak>
    <#include "includes/peer_review/peer_review_section_header_inc.ftl" />                                    
    <div ng-if="workspaceSrvc.displayPeerReview" class="workspace-accordion-content">
        <#include "includes/peer_review/public_peer_review_body_list.ftl" />
    </div>           
</div>
    
    