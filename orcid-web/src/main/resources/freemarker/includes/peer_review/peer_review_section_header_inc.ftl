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
<div class="workspace-accordion-header">
    <div class="row">
        <div class="col-md-3 col-sm-2 col-xs-12">
            <div class="workspace-title" ng-controller="WorkspaceSummaryCtrl">
                <a href="" ng-click="workspaceSrvc.togglePeerReviews($event)" class="toggle-text">
                   <i class="glyphicon-chevron-down glyphicon x075" ng-class="{'glyphicon-chevron-right':workspaceSrvc.displayPeerReview==false}"></i>
                   <@orcid.msg 'workspace_peer_review_body_list.peerReview'/> (<span ng-bind="peerReviewSrvc.peerReviewCount()"></span>)
                </a>
            </div>
        </div>
        <div ng-show="workspaceSrvc.displayWorks" class="col-md-9 col-sm-10 col-xs-12 action-button-bar">
            <#include "../workspace/workspace_act_sort_menu.ftl"/>
        </div>        
    </div>
</div>
