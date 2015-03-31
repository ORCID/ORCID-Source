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
<#include "includes/peer_review/del_peer_review_inc.ftl"/>

<#include "includes/peer_review/add_peer_review_inc.ftl"/>
<div ng-controller="PeerReviewCtrl">
	<div class="workspace-accordion" id="workspace-accordion">
		<div id="workspace-peer-review" class="workspace-accordion-item workspace-accordion-active">
			<div class="workspace-accordion-header"><a name='workspace-peer-review'></a>
				
			    <a href="" ng-click="workspaceSrvc.togglePeerReview()" class="toggle-text">
			  		<i class="glyphicon-chevron-down glyphicon x0" ng-class="{'glyphicon-chevron-right':workspaceSrvc.displayPeerReview==false}"></i></a>
			   	</a> 
			    <a href="" ng-click="workspaceSrvc.togglePeerReview()" class="toggle-text"><@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.education'/></a>
				<a href="" class="label btn-primary" ng-click="addPeerReviewModal()"><@orcid.msg 'manual_affiliation_form_contents.add_education_manually'/></a>
				
			</div>
			<div class="workspace-accordion-content" ng-show="1 == 1" >
			<!-- <div class="workspace-accordion-content" ng-show="workspaceSrvc.displayPeerReview" >  -->
				<#include "includes/peer_review/peer_review_body_inc.ftl" />
			</div>
			
		
		</div>
	</div>
</div>