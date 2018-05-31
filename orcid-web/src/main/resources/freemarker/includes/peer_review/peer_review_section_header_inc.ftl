<div class="workspace-accordion-header clearfix">
    <div class="row">
        <div class="col-md-4 col-sm-4 col-xs-12">
            <div class="workspace-title" ng-controller="WorkspaceSummaryCtrl">
                <a href="" ng-click="workspaceSrvc.togglePeerReviews($event)" class="toggle-text">
                   <i class="glyphicon-chevron-down glyphicon x075" ng-class="{'glyphicon-chevron-right':workspaceSrvc.displayPeerReview==false}"></i>
                   <@orcid.msg 'workspace_peer_review_body_list.peerReview'/> (<span ng-bind="peerReviewSrvc.peerReviewCount()"></span>)
                </a>
                <#if !(isPublicProfile??)> 
                    <div class="popover-help-container">
                        <i class="glyphicon glyphicon-question-sign"></i>
                        <div id="peer-review-help" class="popover bottom">
                            <div class="arrow"></div>
                            <div class="popover-content">
                                <p><@orcid.msg 'manage_peer_review_settings.helpPopoverPeerReview'/> <a href="${knowledgeBaseUri}/articles/1807594" target="manage_peer_review_settings.helpPopoverPeerReview"><@orcid.msg 'common.learn_more'/></a></p>
                            </div>
                        </div>
                    </div>
                </#if>
            </div>
        </div>
        <div ng-show="workspaceSrvc.displayWorks" class="col-md-8 col-sm-8 col-xs-12 action-button-bar">
            <#include "../workspace/workspace_act_sort_menu.ftl"/>
        </div>        
    </div>
</div>
