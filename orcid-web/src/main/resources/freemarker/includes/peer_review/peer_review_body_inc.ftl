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
                            <h3 class="workspace-title">
                                <span ng-bind="peerReview.subjectForm.title.value"></span>
                                <span class="journaltitle" ng-show="peerReview.subjectForm.journalTitle.value" ng-bind="peerReview.subjectForm.journalTitle.value"></span>                                
                            </h3>                                                        
                            <div class="info-detail">
                                <span ng-show="peerReview.completionDate.year">{{peerReview.completionDate.year}}</span><span ng-show="peerReview.completionDate.month">-{{peerReview.completionDate.month}}</span><span ng-show="peerReview.completionDate.year"> | </span> <span class="capitalize">{{peerReview.subjectForm.workType.value}}</span>
                            </div>
                        </div>
                    
                    	<div class="col-md-3 col-sm-3 col-xs-4 workspace-toolbar">
                              <ul class="workspace-private-toolbar" ng-hide="editSources[group.groupId] == true">
                                  <#if !(isPublicProfile??)>
                                      <!-- Bulk edit tool -->
                                      <li ng-show="bulkEditShow == true" class="bulk-checkbox-item">
                                              <input type="checkbox" ng-model="bulkEditMap[peerReview.putCode.value]" class="bulk-edit-input ng-pristine ng-valid pull-right">       
                                      </li>
                                  </#if>
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
                     <!-- Active Row Identifiers / URL / Validations / Versions -->
                     <div class="row" ng-show="group.activePutCode == peerReview.putCode.value">
                         <div class="col-md-12 col-sm-12 bottomBuffer">
                             <ul class="id-details">
                                 <li>
                                     <span ng-repeat='ie in peerReview.externalIdentifiers'><span
                                     ng-bind-html='ie | workExternalIdentifierHtml:$first:$last:peerReview.externalIdentifiers.length'></span>
                                    </span>
                                 </li>
                                 <li ng-show="peerReview.url.value"><@orcid.msg 'common.url' />: <a href="{{peerReview.url.value | urlWithHttp}}" class="truncate-anchor" target="_blank">{{peerReview.url.value}}</a></li>
                             </ul>
                         </div>
                     </div>
                    <!-- more info -->
                    <#include "peer_review_more_info_inc.ftl"/>
                    
                    <!-- active row  source display -->
                      <div class="row source-line" ng-show="group.activePutCode == peerReview.putCode.value">
                          <div class="col-md-7 col-sm-7 col-xs-7" ng-show="editSources[group.groupId] == true">
                              {{peerReview.sourceName}}
                          </div>
                          <div class="col-md-3 col-sm-3 col-xs-3" ng-show="editSources[group.groupId] == true">

                            <div ng-show="editSources[group.groupId] == true">
                                <span class="glyphicon glyphicon-check ng-hide" ng-show="peerReview.putCode.value == group.defaultPutCode"></span><span ng-show="peerReview.putCode.value == group.defaultPutCode"> <@orcid.msg 'groups.common.preferred_source' /></span>
                                <#if !(isPublicProfile??)>
                                    <a ng-click="peerReviewSrvc.makeDefault(group, peerReview.putCode.value); " ng-show="peerReview.putCode.value != group.defaultPutCode" class="">
                                         <span class="glyphicon glyphicon-unchecked"></span> <@orcid.msg 'groups.common.make_preferred' />
                                    </a>
                                </#if>
                            </div>

                          </div>
                          <div class="col-md-2 trash-source" ng-show="editSources[group.groupId] == true">
                              <div ng-show="editSources[group.groupId] == true">
                            <#if !(isPublicProfile??)>
                                <ul class="sources-actions">
                                    <#if RequestParameters['combine']??>
                                        <li ng-show="canBeCombined(peerReview)">
                                            <a class="glyphicons git_pull_request" ng-click="showCombineMatches(group.getDefault())" ng-mouseenter="showTooltip(peerReview.putCode.value+'-combineActiveDuplicates')" ng-mouseleave="hideTooltip(peerReview.putCode.value+'-combineActiveDuplicates')"></a>

                                            <div class="popover popover-tooltip top combine-activeDuplicates-popover" ng-show="showElement[peerReview.putCode.value+'-combineActiveDuplicates'] == true">
                                                <div class="arrow"></div>
                                                <div class="popover-content">
                                                    <@orcid.msg 'groups.common.combine_duplicates' />
                                                </div>
                                            </div>


                                        </li>
                                    </#if>
                                    <li> 
                                        <@orcid.editActivityIcon
                                            activity="peerReview"
                                            click="openEditPeerReview(peerReview.putCode.value)"
                                            toolTipSuffix="editToolTipSource"
                                            toolTipClass="popover popover-tooltip top edit-activeSource-popover"
                                         />
                                    </li>
                                    <li>
                                        <a ng-click="deletePeerReviewConfirm(peerReview.putCode.value, false)"  title="<@orcid.msg 'freemarker.btnDelete' /> {{peerReview.subjectForm.title.value}}" ng-mouseenter="showTooltip(peerReview.putCode.value+'-deleteActiveSource')" ng-mouseleave="hideTooltip(peerReview.putCode.value+'-deleteActiveSource')">
                                            <span class="glyphicon glyphicon-trash"></span>
                                        </a>

                                        <div class="popover popover-tooltip top delete-activeSource-popover" ng-show="showElement[peerReview.putCode.value+'-deleteActiveSource'] == true">
                                            <div class="arrow"></div>
                                            <div class="popover-content">
                                                <@orcid.msg 'groups.common.delete_this_source' />
                                            </div>
                                        </div>
                                    </li>
                                </ul>
                            </#if>
                        	</div>
                          </div>
                    </div>
                    
                    <!-- not active row && edit sources -->
                    <div ng-show="group.activePutCode != peerReview.putCode.value" class="row source-line">
                        <div class="col-md-7 col-sm-7 col-xs-7">
                            <a ng-click="group.activePutCode = peerReview.putCode.value;">
                                {{peerReview.sourceName}}
                            </a>
                        </div>
                        
                        <div class="col-md-3 col-sm-3 col-xs-3">
                             <#if !(isPublicProfile??)>
                                <span class="glyphicon glyphicon-check" ng-show="peerReview.putCode.value == group.defaultPutCode"></span><span ng-show="peerReview.putCode.value == group.defaultPutCode"> <@orcid.msg 'groups.common.preferred_source' /></span>
                                <a ng-click="peerReviewSrvc.makeDefault(group, peerReview.putCode.value); " ng-show="peerReview.putCode.value != group.defaultPutCode">
                                   <span class="glyphicon glyphicon-unchecked"></span> <@orcid.msg 'groups.common.make_preferred' />
                                </a>
                            </#if>
                        </div>
                        <div class="col-md-2 col-sm-2 col-xs-2 trash-source">
                            <#if !(isPublicProfile??)>
                                <ul class="sources-actions">
                                    <#if RequestParameters['combine']??>
                                        <li ng-show="canBeCombined(peerReview)">
                                            <a class="glyphicons git_pull_request" ng-click="showCombineMatches(group.getDefault())" ng-mouseenter="showTooltip(peerReview.putCode.value+'-combineInactiveDuplicates')" ng-mouseleave="hideTooltip(peerReview.putCode.value+'-combineInactiveDuplicates')"></a>

                                            <div class="popover popover-tooltip top combine-inactiveDuplicates-popover" ng-show="showElement[peerReview.putCode.value+'-combineInactiveDuplicates'] == true">
                                                <div class="arrow"></div>
                                                <div class="popover-content">
                                                    <@orcid.msg 'groups.common.combine_duplicates' />
                                                </div>
                                            </div>

                                        </li>
                                    </#if>
                                    <li> 
                                        <@orcid.editActivityIcon
                                            activity="peerReview"
                                            click="openEditPeerReview(peerReview.putCode.value)"
                                            toolTipSuffix="editToolTipSourceActions"
                                            toolTipClass="popover popover-tooltip top edit-inactiveSource-popover"
                                         />
                                    </li>
                                    <li>
                                        <a ng-click="deletePeerReviewConfirm(peerReview.putCode.value, false)" ng-mouseenter="showTooltip(peerReview.putCode.value+'-deleteInactiveSource')" ng-mouseleave="hideTooltip(peerReview.putCode.value+'-deleteInactiveSource')">
                                            <span class="glyphicon glyphicon-trash" title="<@orcid.msg 'freemarker.btnDelete'/> {{peerReview.subjectForm.title.value}}"></span>
                                        </a>

                                        <div class="popover popover-tooltip top delete-inactiveSource-popover" ng-show="showElement[peerReview.putCode.value+'-deleteInactiveSource'] == true">
                                            <div class="arrow"></div>
                                            <div class="popover-content">
                                               <@orcid.msg 'groups.common.delete_this_source' />
                                            </div>
                                        </div>
                                    </li>
                                </ul>
                            </#if>
                        </div>
                    </div>
                    
                    <!--  Bottom row -->                     
                    <div class="row source-line" ng-hide="editSources[group.groupId] == true">                        
                        
                        <div class="col-md-7 col-sm-7 col-xs-7">
                             <@orcid.msg 'groups.common.source'/>: {{peerReview.sourceName}}
                        </div>
                        
                        <div class="col-md-3 col-sm-3 col-xs-3">
                              <span class="glyphicon glyphicon-check"></span><span> <@orcid.msg 'groups.common.preferred_source' /></span> <span ng-hide="group.activitiesCount == 1">(</span><a ng-click="showSources(group)" ng-hide="group.activitiesCount == 1" ng-mouseenter="showTooltip(group.groupId+'-sources')" ng-mouseleave="hideTooltip(group.groupId+'-sources')"><@orcid.msg 'groups.common.of'/> {{group.activitiesCount}}</a><span ng-hide="group.activitiesCount == 1">)</span>

                              <div class="popover popover-tooltip top sources-popover" ng-show="showElement[group.groupId+'-sources'] == true">
                                   <div class="arrow"></div>
                                   <div class="popover-content">
                                       <@orcid.msg 'groups.common.sources.show_other_sources' />
                                   </div>
                              </div>
                        </div>

                        <div class="col-md-2 col-sm-2 col-xs-2" ng-show="group.activePutCode == peerReview.putCode.value">
                            <ul class="sources-options" ng-cloak>
                                <#if !(isPublicProfile??)>
                                    <#if RequestParameters['combine']??>
                                        <li ng-show="canBeCombined(peerReview)">
                                            <a ng-click="showCombineMatches(group.getDefault())" title="<@orcid.msg 'groups.common.combine_duplicates' />" ng-mouseenter="showTooltip(group.groupId+'-combineDuplicates')" ng-mouseleave="hideTooltip(group.groupId+'-combineDuplicates')">
                                                <span class="glyphicons git_pull_request"></span>
                                            </a>

                                            <div class="popover popover-tooltip top combine-duplicates-popover" ng-show="showElement[group.groupId+'-combineDuplicates'] == true">
                                                <div class="arrow"></div>
                                                <div class="popover-content">
                                                    <@orcid.msg 'groups.common.combine_duplicates' />
                                                </div>
                                            </div>
                                        </li>
                                    </#if>

                                    <li>
                                        <@orcid.editActivityIcon
                                            activity="peerReview"
                                            click="openEditPeerReview(peerReview.putCode.value)"
                                            toolTipSuffix="editToolTip"
                                            toolTipClass="popover popover-tooltip top edit-source-popover"
                                         />
                                    </li>

                                     <li ng-hide="editSources[group.groupId] == true || group.activitiesCount == 1">
                                        <a ng-click="showSources(group)" ng-mouseenter="showTooltip(group.groupId+'-deleteGroup')" ng-mouseleave="hideTooltip(group.groupId+'-deleteGroup')">
                                            <span class="glyphicon glyphicon-trash"></span>
                                        </a>
                                        <div class="popover popover-tooltip top delete-group-popover" ng-show="showElement[group.groupId+'-deleteGroup'] == true">
                                             <div class="arrow"></div>
                                            <div class="popover-content">
                                               <@orcid.msg 'groups.common.delete_this_source' />
                                            </div>
                                        </div>
                                     </li>

                                     <li ng-show="group.activitiesCount == 1">
                                        <a ng-click="deletePeerReviewConfirm(group.getActive().putCode.value, false)" ng-mouseenter="showTooltip(group.groupId+'-deleteSource')" ng-mouseleave="hideTooltip(group.groupId+'-deleteSource')">
                                           <span class="glyphicon glyphicon-trash"></span>
                                        </a>
                                        <div class="popover popover-tooltip top delete-source-popover" ng-show="showElement[group.groupId+'-deleteSource'] == true">
                                             <div class="arrow"></div>
                                            <div class="popover-content">
                                                  <@orcid.msg 'groups.common.delete_this_source' />
                                            </div>
                                        </div>
                                    </li>
                                  </#if>
                            </ul>
                        </div>
                    </div>
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
<!--  <div ng-show="worksSrvc.loading == false && worksSrvc.groups.length == 0" class="" ng-cloak> -->
    <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_peer_review_body_list.Nopublicationsaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_peer_review_body_list.havenotaddedanypeerreviews")} <a ng-click="addPeerReviewModal()">${springMacroRequestContext.getMessage("workspace_peer_review_body_list.addsomenow")}</a></#if></strong>
</div>