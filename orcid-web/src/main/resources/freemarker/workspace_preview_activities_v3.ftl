<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<#if !(affiliationsEmpty)??>
    <!-- Education -->
    <div id="workspace-education" class="workspace-accordion-item workspace-accordion-active" ng-controller="PublicEduAffiliation" ng-hide="!affiliationsSrvc.educations.length" ng-cloack>        
    
        <#include "includes/affiliate/edu_section_header_inc.ftl" />
        <div ng-if="workspaceSrvc.displayEducation" class="workspace-accordion-content">
            <#include "includes/affiliate/edu_body_inc.ftl" />
        </div>           
    </div>
    <!-- Employment -->
    <div id="workspace-employment" class="workspace-accordion-item workspace-accordion-active" ng-controller="PublicEmpAffiliation" ng-hide="!affiliationsSrvc.employments.length" ng-cloack>
        <#include "includes/affiliate/emp_section_header_inc.ftl" />
        <div ng-if="workspaceSrvc.displayEmployment" class="workspace-accordion-content">
            <#include "includes/affiliate/emp_body_inc.ftl" />  
        </div>
    </div>
</#if>
  
<!-- Funding -->
<#if !(fundingEmpty)??>     
    <div id="workspace-funding" class="workspace-accordion-item workspace-accordion-active" ng-controller="PublicFundingCtrl" ng-cloack>
        <#include "includes/funding/funding_section_header_inc_v3.ftl" />
        <div ng-if="workspaceSrvc.displayFunding" class="workspace-accordion-content">
            <#include "includes/funding/body_funding_inc_v3.ftl" /> 
        </div>
    </div>
</#if>

<!-- Works -->
<#if !(worksEmpty)??>       
    <div id="workspace-works" class="workspace-accordion-item workspace-accordion-active" ng-controller="PublicWorkCtrl" ng-cloak>
        <#include "includes/work/work_section_header_inc_v3.ftl"/>
        <div ng-if="workspaceSrvc.displayWorks" class="workspace-accordion-content">
            <#include "includes/work/public_works_body_list.ftl" />
        </div>
    </div>
</#if>

<!-- Peer Review -->
<#if !(peerReviewsEmpty)??> 
    <div id="workspace-peer-review" class="workspace-accordion-item workspace-accordion-active" ng-controller="PublicPeerReviewCtrl" ng-cloack>
        <#include "includes/peer_review/peer_review_section_header_inc.ftl" />                                    
        <div ng-if="workspaceSrvc.displayPeerReview" class="workspace-accordion-content">
            <#include "includes/peer_review/public_peer_review_body_list.ftl" />
        </div>           
    </div>
</#if>
    
    