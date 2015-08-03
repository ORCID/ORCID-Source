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
			<!-- Header -->
             <li ng-show="editSources[group.groupId] == true" class="source-header" ng-class="{'source-active' : editSources[group.groupId] == true}" ng-model="group.activities">
                 <div class="sources-header">
                     <div class="row">
                         <div class="col-md-7 col-sm-7 col-xs-7">
                         	<@orcid.msg 'groups.common.sources' /> <span class="hide-sources" ng-click="hideSources(group)"><@orcid.msg 'groups.common.close_sources' /></span>
                         </div>
                         <div class="col-md-2 col-sm-2 col-xs-2">
                             <@orcid.msgCapFirst 'groups.common.preferred' />
                         </div>
                         <div class="col-md-3 col-sm-3 col-xs-3 right">
	                         <div class="workspace-toolbar">
	                             <ul class="workspace-private-toolbar">
	                                 <#if !(isPublicProfile??)>
	                                     <li ng-show="bulkEditShow">
	                                         <input type="checkbox" ng-model="bulkEditMap[group.getActive().putCode.value]" class="bulk-edit-input-header ng-valid ng-dirty">
	                                     </li>
	                                 </#if>
	                                
	                                 <li class="works-details">
	                                     <a ng-click="showDetailsMouseClick(group.groupId,$event);" ng-mouseenter="showTooltip(group.groupId+'-showHideDetails')" ng-mouseleave="hideTooltip(group.groupId+'-showHideDetails')">
	                                         <span ng-class="(showDetails[group.groupId] == true) ? 'glyphicons collapse_top' : 'glyphicons expand'">
	                                         </span>
	                                     </a>
	                                     <div class="popover popover-tooltip top show-hide-details-popover" ng-show="showElement[group.groupId+'-showHideDetails'] == true">
	                                          <div class="arrow"></div>
	                                         <div class="popover-content">   
	                                             <span ng-show="showDetails[group.groupId] == false || showDetails[group.groupId] == null"><@orcid.msg 'common.details.show_details'/></span>   
	                                             <span ng-show="showDetails[group.groupId] == true"><@orcid.msg 'common.details.hide_details'/></span>
	                                         </div>
	                                     </div>
	                                 </li>
	                                
	                                 <#if !(isPublicProfile??)>
	                                 <li>
	                                     <@orcid.privacyToggle2 angularModel="group.getActive().visibility"
	                                         questionClick="toggleClickPrivacyHelp(group.getActive().putCode)"
	                                         clickedClassCheck="{'popover-help-container-show':privacyHelp[group.getActive().putCode.value]==true}"
	                                         publicClick="peerReviewSrvc.setGroupPrivacy(group.getActive().putCode.value, 'PUBLIC', $event)"
	                                         limitedClick="peerReviewSrvc.setGroupPrivacy(group.getActive().putCode.value, 'LIMITED', $event)"
	                                         privateClick="peerReviewSrvc.setGroupPrivacy(group.getActive().putCode.value, 'PRIVATE', $event)"/>
	                                 </li>
	                                 </#if>
	                             </ul>
	                         </div>
                         </div>
                     </div>


                 </div>
             </li>
             <!-- End of Header -->
             <li ng-repeat="peerReview in group.activities" ng-show="group.activePutCode == peerReview.putCode.value || editSources[group.groupId] == true" orcid-put-code="{{peerReview.putCode.value}}">
                    <!-- active row summary info -->
                    <div class="row" ng-show="group.activePutCode == peerReview.putCode.value">
                        <div class="col-md-9 col-sm-9 col-xs-8">
                        	<div>
                        		<span class="title" ng-click="showDetailsMouseClick(group.groupId,$event);"><span ng-class="{'glyphicon x075 glyphicon-chevron-right': showDetails[group.groupId] == false || showDetails[group.groupId] == null, 'glyphicon x075 glyphicon-chevron-down': showDetails[group.groupId] == true}"></span> <span>review activity for </span><span class="peer-review-title" ng-bind="peerReview.groupId.value"></span></span> ({{peerReviewSrvc.peerReviewCount}})
                        	</div>
                        </div>
                    
                    	<div class="col-md-3 col-sm-3 col-xs-4 workspace-toolbar">
                              <ul class="workspace-private-toolbar" ng-hide="editSources[group.groupId] == true">
                                  <!-- Show/Hide Details -->
                                  <li class="works-details" ng-hide="editSources[group.groupId] == true">
                                      <a ng-click="showDetailsMouseClick(group.groupId,$event);" ng-mouseenter="showTooltip(group.groupId+'-showHideDetails')" ng-mouseleave="hideTooltip(group.groupId+'-showHideDetails')">
                                          <span ng-class="(showDetails[group.groupId] == true) ? 'glyphicons collapse_top' : 'glyphicons expand'">
                                          </span>
                                      </a>
                                      <div class="popover popover-tooltip top show-hide-details-popover" ng-show="showElement[group.groupId+'-showHideDetails'] == true">
                                           <div class="arrow"></div>
                                          <div class="popover-content">
                                              <span ng-show="showDetails[group.groupId] == false || showDetails[group.groupId] == null"><@orcid.msg 'common.details.show_details' /></span>   
                                              <span ng-show="showDetails[group.groupId] == true"><@orcid.msg 'common.details.hide_details' /></span>
                                          </div>
                                      </div>
                                  </li>

                                  <#if !(isPublicProfile??)>
                                      <!-- Privacy -->
                                      <li>
                                          <@orcid.privacyToggle2 angularModel="peerReview.visibility"
                                              questionClick="toggleClickPrivacyHelp(group.highestVis())"
                                              clickedClassCheck="{'popover-help-container-show':privacyHelp[peerReview.putCode.value]==true}"
                                              publicClick="peerReviewSrvc.setGroupPrivacy(peerReview.putCode.value, 'PUBLIC', $event)"
                                              limitedClick="peerReviewSrvc.setGroupPrivacy(peerReview.putCode.value, 'LIMITED', $event)"
                                              privateClick="peerReviewSrvc.setGroupPrivacy(peerReview.putCode.value, 'PRIVATE', $event)"/>
                                      </li>
                                  </#if>
                              </ul>
                               
                              <#if !(isPublicProfile??)>
                                  <div ng-show="!group.consistentVis() && !editSources[group.groupId]" class="vis-issue">
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
<div ng-show="peerReviewSrvc.loading == false && peerReviewSrvc.groups.length == 0" class="" ng-cloak>
    <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_peer_review_body_list.Nopublicationsaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_peer_review_body_list.havenotaddedanypeerreviews")} <a ng-click="addPeerReviewModal()">${springMacroRequestContext.getMessage("workspace_peer_review_body_list.addsomenow")}</a></#if></strong>
</div>