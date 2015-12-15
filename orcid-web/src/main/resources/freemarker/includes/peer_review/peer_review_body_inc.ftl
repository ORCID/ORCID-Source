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
<ul ng-hide="!peerReviewSrvc.groups.length" class="workspace-peer-review workspace-body-list bottom-margin-medium" ng-cloak>
	<li class="bottom-margin-small workspace-border-box card" ng-repeat="group in peerReviewSrvc.groups | orderBy:sortState.predicate:sortState.reverse">
		<ul class="sources-edit-list">
			 <li class="peer-review-group" ng-repeat="peerReview in group.activities | orderBy: ['groupName']" ng-show="group.activePutCode == peerReview.putCode.value" orcid-put-code="{{peerReview.putCode.value}}" class="group-details">
			 	<!-- active row summary info -->
                <div class="row">
                    <div class="col-md-9 col-sm-9 col-xs-8">                    	
                    	<div ng-init="peerReviewSrvc.getPeerReviewGroupDetails(group.groupRealId, peerReview.putCode.value)">
                    		<span class="title" ng-click="showDetailsMouseClick(group.groupId,$event);"><span ng-class="{'glyphicon x075 glyphicon-chevron-right': showDetails[group.groupId] == false || showDetails[group.groupId] == null, 'glyphicon x075 glyphicon-chevron-down': showDetails[group.groupId] == true}"></span> <span>review activity for </span><span class="peer-review-title"><span ng-bind="group.groupName"></span>({{group.activitiesCount}})</span></span>
                    	</div>
                    </div>
                
                	<div class="col-md-3 col-sm-3 col-xs-4 workspace-toolbar">
                          <ul class="workspace-private-toolbar">
                              <#if !(isPublicProfile??)>
                                  <!-- Privacy -->
                                  <li>
                                      <@orcid.privacyToggle2 angularModel="peerReview.visibility"
                                          questionClick=""
                                          clickedClassCheck="{'popover-help-container-show':privacyHelp[peerReview.putCode.value]==true}"
                                          publicClick="peerReviewSrvc.setGroupPrivacy(peerReview.putCode.value, 'PUBLIC', $event)"
                                          limitedClick="peerReviewSrvc.setGroupPrivacy(peerReview.putCode.value, 'LIMITED', $event)"
                                          privateClick="peerReviewSrvc.setGroupPrivacy(peerReview.putCode.value, 'PRIVATE', $event)"/>
                                  </li>
                              </#if>
                          </ul>
                           
                          <#if !(isPublicProfile??)>
                            <div ng-show="!group.consistentVis()" class="vis-issue">
                             	<div class="popover-help-container">
			                    <span class="glyphicons circle_exclamation_mark" ng-mouseleave="hideTooltip('vis-issue')" ng-mouseenter="showTooltip('vis-issue')"></span>
			                    <div class="popover vis-popover bottom" ng-show="showElement['vis-issue'] == true">
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
<div ng-show="" class="text-center">
    <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
    <!--[if lt IE 8]>    
    	<img src="${staticCdn}/img/spin-big.gif" width="85" height ="85"/>
    <![endif]-->
</div>
<div ng-show="peerReviewSrvc.loading == false && peerReviewSrvc.groups.length == 0" ng-cloak>
    <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_peer_review_body_list.Nopublicationsaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_peer_review_body_list.havenotaddedanypeerreviews")} <a ng-show="noLinkFlag" ng-click="showPeerReviewImportWizard()" class="no-wrap">${springMacroRequestContext.getMessage("workspace_peer_review_body_list.addsomenow")}</a></#if></strong>
</div>