<ul ng-hide="!peerReviewSrvc.groups.length" class="workspace-peer-review workspace-body-list bottom-margin-medium" id="peer-review-header" ng-cloak>
    <li class="bottom-margin-small workspace-border-box card" ng-repeat="group in peerReviewSrvc.groups | orderBy:sortState.predicate:sortState.reverse" >
        <ul class="sources-edit-list">
            <li class="peer-review-group" ng-repeat="peerReview in group.activities | orderObjectBy: ['groupName']" ng-if="group.activePutCode == peerReview.putCode.value" orcid-put-code="peerReview.putCode.value" class="group-details">
                <!-- active row summary info -->
                <div class="row">
                    <div class="col-md-9 col-sm-9 col-xs-8">
                        <div ng-init="peerReviewSrvc.getPeerReviewGroupDetails(peerReview.groupIdPutCode.value, peerReview.putCode.value)">
                            <span class="title" ng-click="showDetailsMouseClick(group.groupId,$event);"><span ng-class="{'glyphicon x075 glyphicon-chevron-right': showDetails[group.groupId] == false || showDetails[group.groupId] == null, 'glyphicon x075 glyphicon-chevron-down': showDetails[group.groupId] == true}"></span> <span><@orcid.msg 'peer_review.review_activity_for' /> </span><span class="peer-review-title"><span ng-bind="group.groupName"></span>(<span ng-bind="group.activitiesCount"></span>)</span></span>
                        </div>
                    </div>
                    <div class="col-md-3 col-sm-3 col-xs-4 workspace-toolbar">
                        <ul class="workspace-private-toolbar">
                            <#if !(isPublicProfile??)>
                            <!-- Privacy -->
                            <li>
                            <@orcid.privacyToggle2 angularModel="peerReview.visibility.visibility"
                            questionClick=""
                            clickedClassCheck="{'popover-help-container-show':privacyHelp[peerReview.putCode.value]==true}"
                            publicClick="peerReviewSrvc.setGroupPrivacy(peerReview.putCode.value, 'PUBLIC', $event)"
                            limitedClick="peerReviewSrvc.setGroupPrivacy(peerReview.putCode.value, 'LIMITED', $event)"
                            privateClick="peerReviewSrvc.setGroupPrivacy(peerReview.putCode.value, 'PRIVATE', $event)"/>
                            </li>
                            </#if>
                        </ul>

                        <#if !(isPublicProfile??)>
                        <div ng-if="!group.consistentVis()" class="vis-issue">
                            <div class="popover-help-container">
                                <span class="glyphicons circle_exclamation_mark" ng-mouseleave="hideTooltip('vis-issue')" ng-mouseenter="showTooltip('vis-issue')"></span>
                                <div class="popover vis-popover bottom" ng-if="showElement['vis-issue'] == true">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <@orcid.msg 'groups.common.data_inconsistency' />                                            
                                    </div>
                                </div>
                            </div>                                    
                        </div>
                        </#if> 

                    </div>
                </div>
                <!-- more info -->
                <#include "peer_review_more_info_inc.ftl"/>
            </li>
        </ul>
    </li>	
</ul>
<div ng-if="peerReviewSrvc.loading == false && peerReviewSrvc.groups.length == 0" ng-cloak>
    <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_peer_review_body_list.Nopublicationsaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_peer_review_body_list.havenotaddedanypeerreviews")} <a ng-if="noLinkFlag" ng-click="showPeerReviewImportWizard()" class="no-wrap">${springMacroRequestContext.getMessage("workspace_peer_review_body_list.addsomenow")}</a></#if></strong>
</div>