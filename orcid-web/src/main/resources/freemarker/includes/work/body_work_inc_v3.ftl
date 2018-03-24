<ul ng-hide="!worksSrvc.groups.length" class="workspace-publications bottom-margin-medium" id="body-work-list" ng-cloak>
    

    <li class="bottom-margin-small workspace-border-box card" ng-repeat="group in worksSrvc.groups">
        <div class="work-list-container">
            <ul class="sources-edit-list">
                <!-- Header -->
                <li ng-if="editSources[group.groupId]" class="source-header" ng-class="{'source-active' : editSources[group.groupId] == true}" ng-model="group.works">
                    <div class="sources-header">
                        <div class="row">
                            <div class="col-md-7 col-sm-7 col-xs-6">
                                <@orcid.msg 'groups.common.sources' /> <span class="hide-sources" ng-click="hideSources(group)"><@orcid.msg 'groups.common.close_sources' /></span>
                            </div>
                            
                            <div class="col-md-2 col-sm-2 hidden-xs">
                                <@orcid.msgCapFirst 'groups.common.preferred' />
                            </div>
                            
                            <div class="col-md-3 col-sm-3 col-xs-6 right padding-left-fix">
                                    <div class="workspace-toolbar">
                                        <ul class="workspace-private-toolbar">
                                            <#if !(isPublicProfile??)>
                                                <li ng-if="bulkEditShow">
                                                    <input type="checkbox" ng-model="bulkEditMap[group.activePutCode.value]" class="bulk-edit-input-header ng-valid ng-dirty">
                                                </li>
                                            </#if>                                                                                  
                                            <li class="works-details">
                                                <a ng-click="showDetailsMouseClick(group,$event);showMozillaBadges(group.activePutCode)" ng-mouseenter="showTooltip(group.groupId+'-showHideDetails')" ng-mouseleave="hideTooltip(group.groupId+'-showHideDetails')">
                                                    <span ng-class="(moreInfo[group.groupId] == true) ? 'glyphicons collapse_top' : 'glyphicons expand'">
                                                    </span>
                                                </a>
                                                <div class="popover popover-tooltip top show-hide-details-popover" ng-if="showElement[group.groupId+'-showHideDetails']">
                                                     <div class="arrow"></div>
                                                    <div class="popover-content">   
                                                        <span ng-if="moreInfo[group.groupId] == false || moreInfo[group.groupId] == null"><@orcid.msg 'common.details.show_details'/></span>   
                                                        <span ng-if="moreInfo[group.groupId]"><@orcid.msg 'common.details.hide_details'/></span>
                                                    </div>
                                                </div>
                                            </li>
                                            <#if !(isPublicProfile??)>
                                                <li>
                                                    <@orcid.privacyToggle2 angularModel="group.activeVisibility"
                                                        questionClick="toggleClickPrivacyHelp(group.activePutCode)"
                                                        clickedClassCheck="{'popover-help-container-show':privacyHelp[group.activePutCode]==true}"
                                                        publicClick="worksSrvc.setGroupPrivacy(group.activePutCode, 'PUBLIC', $event)"
                                                        limitedClick="worksSrvc.setGroupPrivacy(group.activePutCode, 'LIMITED', $event)"
                                                        privateClick="worksSrvc.setGroupPrivacy(group.activePutCode, 'PRIVATE', $event)"/>
                                                </li>
                                            </#if>
                                        </ul>
                                    </div>
                            </div>
                        </div>
                    </div>
                </li>
                <!-- End of Header -->


                <li ng-repeat="work in group.works" ng-if="group.activePutCode == work.putCode.value || editSources[group.groupId] == true" orcid-put-code="{{work.putCode.value}}">

                    <!-- active row summary info -->
                    <div class="row" ng-if="group.activePutCode == work.putCode.value">
                        <div class="col-md-9 col-sm-9 col-xs-7">
                            <h3 class="workspace-title">
                                <span ng-bind="work.title.value"></span>
                                <span class="journaltitle" ng-if="work.journalTitle.value" ng-bind="work.journalTitle.value"></span>                                
                            </h3>                                                        
                            <div class="info-detail">
                                <span ng-if="work.publicationDate.year" ng-bind="work.publicationDate.year"></span><span ng-if="work.publicationDate.month">-{{work.publicationDate.month}}</span><span ng-if="work.publicationDate.day">-</span><span ng-if="work.publicationDate.day" ng-bind="work.publicationDate.day"></span><span ng-if="work.publicationDate.year"> | </span> <span class="capitalize" ng-bind="work.workType.value"></span>
                            </div>
                        </div>


                          <div class="col-md-3 col-sm-3 col-xs-5 workspace-toolbar">
                              <ul class="workspace-private-toolbar" ng-hide="editSources[group.groupId] == true">                                 
                                  <#if !(isPublicProfile??)>
                                      <!-- Bulk edit tool -->
                                       <li ng-if="bulkEditShow" class="bulk-checkbox-item">
                                          <input type="checkbox" ng-model="bulkEditMap[work.putCode.value]" class="bulk-edit-input ng-pristine ng-valid pull-right">       
                                       </li>
                                  </#if>                                     
                                  <!-- Show/Hide Details -->
                                  <li class="works-details" ng-hide="editSources[group.groupId] == true">
                                      <a ng-click="showDetailsMouseClick(group,$event);showMozillaBadges(group.activePutCode)" ng-mouseenter="showTooltip(group.groupId+'-showHideDetails')" ng-mouseleave="hideTooltip(group.groupId+'-showHideDetails')">
                                          <span ng-class="(moreInfo[group.groupId] == true) ? 'glyphicons collapse_top' : 'glyphicons expand'">
                                          </span>
                                      </a>
                                      <div class="popover popover-tooltip top show-hide-details-popover" ng-if="showElement[group.groupId+'-showHideDetails']">
                                           <div class="arrow"></div>
                                          <div class="popover-content">
                                              <span ng-if="moreInfo[group.groupId] == false || moreInfo[group.groupId] == null"><@orcid.msg 'common.details.show_details' /></span>   
                                              <span ng-if="moreInfo[group.groupId]"><@orcid.msg 'common.details.hide_details' /></span>
                                          </div>
                                      </div>
                                  </li>

                                  <#if !(isPublicProfile??)>
                                      <!-- Privacy -->
                                      <li>
                                          <@orcid.privacyToggle2 angularModel="work.visibility.visibility"
                                              questionClick="toggleClickPrivacyHelp(group.highestVis())"
                                              clickedClassCheck="{'popover-help-container-show':privacyHelp[work.putCode.value]==true}"
                                              publicClick="worksSrvc.setGroupPrivacy(work.putCode.value, 'PUBLIC', $event)"
                                              limitedClick="worksSrvc.setGroupPrivacy(work.putCode.value, 'LIMITED', $event)"
                                              privateClick="worksSrvc.setGroupPrivacy(work.putCode.value, 'PRIVATE', $event)" />
                                      </li>
                                  </#if>
                              </ul>
                               
                              <#if !(isPublicProfile??)>
                                  <div ng-if="!worksSrvc.consistentVis(group) && !editSources[group.groupId]" class="vis-issue">
                                    <div class="popover-help-container">
                                    <span class="glyphicons circle_exclamation_mark" ng-mouseleave="hideTooltip('vis-issue')" ng-mouseenter="showTooltip('vis-issue')"></span>
                                    <div class="popover vis-popover bottom" ng-if="showElement['vis-issue']">
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
                     <div class="row" ng-if="group.activePutCode == work.putCode.value">
                         <div class="col-md-12 col-sm-12 bottomBuffer">
                             <ul class="id-details clearfix">
                                 <li class="url-work clearfix">
                                    <ul class="id-details clearfix">
                                        <li ng-repeat='ie in work.workExternalIdentifiers | orderBy:["-relationship.value", "workExternalIdentifierType.value"]' class="url-popover">
                                            <span bind-html-compile='ie | workExternalIdentifierHtml:$first:$last:work.workExternalIdentifiers.length:moreInfo[group.groupId]'></span>
                                        </li>
                                    </ul>                                   
                                 </li>
                                 <li ng-if="work.url.value" class="url-popover url-work">
                                    <@orcid.msg 'common.url' />: <a href="{{work.url.value | urlProtocol}}" ng-mouseenter="showURLPopOver(work.putCode.value)" ng-mouseleave="hideURLPopOver(work.putCode.value)" ng-class="{'truncate-anchor' : moreInfo[group.groupId] == false || moreInfo[group.groupId] == undefined}" target="work.url.value">{{work.url.value}}</a>
                                    <div class="popover-pos">                                   
                                        <div class="popover-help-container">
                                           <div class="popover bottom" ng-class="{'block' : displayURLPopOver[work.putCode.value] == true}">
                                                <div class="arrow"></div>
                                                <div class="popover-content">
                                                    <a href="{{work.url.value}}" target="work.url.value">{{work.url.value}}</a>
                                                </div>                
                                            </div>                              
                                        </div>
                                     </div>
                                 </li>
                             </ul>
                         </div>
                     </div>


                     <!-- more info -->
                     <#include "work_more_info_inc_v3.ftl"/>

                     <!-- active row  source display -->
                      <div class="row source-line" ng-if="group.activePutCode == work.putCode.value">
                          <div class="col-md-7 col-sm-7 col-xs-12" ng-if="editSources[group.groupId]">
                              {{(work.sourceName == null || work.sourceName == '') ? work.source : work.sourceName }}
                          </div>
                          <div class="col-md-3 col-sm-3 col-xs-10" ng-if="editSources[group.groupId]">

                            <div ng-if="editSources[group.groupId]">
                                <span class="glyphicon glyphicon-check" ng-if="work.putCode.value == group.defaultWork.putCode.value"></span><span ng-if="work.putCode.value == group.defaultWork.putCode.value"> <@orcid.msg 'groups.common.preferred_source' /></span>
                                <#if !(isPublicProfile??)>
                                    <a ng-click="worksSrvc.makeDefault(group, work.putCode.value); " ng-if="work.putCode.value != group.defaultWork.putCode.value">
                                         <span class="glyphicon glyphicon-unchecked"></span> <@orcid.msg 'groups.common.make_preferred' />
                                    </a>
                                </#if>
                            </div>

                          </div>
                          <div class="col-md-2 col-sm-2 trash-source" ng-if="editSources[group.groupId]">
                              <div ng-if="editSources[group.groupId]">
                            <#if !(isPublicProfile??)>
                                <ul class="sources-actions">
                                    <#if RequestParameters['combine']??>
                                        <li ng-if="canBeCombined(work)">
                                            <a class="glyphicons git_pull_request" ng-click="showCombineMatches(group.defaultWork)" ng-mouseenter="showTooltip(work.putCode.value+'-combineActiveDuplicates')" ng-mouseleave="hideTooltip(work.putCode.value+'-combineActiveDuplicates')"></a>

                                            <div class="popover popover-tooltip top combine-activeDuplicates-popover" ng-if="showElement[work.putCode.value+'-combineActiveDuplicates']">
                                                <div class="arrow"></div>
                                                <div class="popover-content">
                                                    <@orcid.msg 'groups.common.combine_duplicates' />
                                                </div>
                                            </div>


                                        </li>
                                    </#if>
                                    <li> 
                                        <@orcid.editWorkIcon
                                            activity="work"
                                            click="openEditWork(work.putCode.value)"
                                            toolTipSuffix="editToolTipSource"
                                            toolTipClass="popover popover-tooltip top edit-activeSource-popover"
                                         />
                                    </li>
                                    <li>
                                        <a ng-click="deleteWorkConfirm(work.putCode.value, false)"  title="<@orcid.msg 'freemarker.btnDelete' /> {{work.title.value}}" ng-mouseenter="showTooltip(work.putCode.value+'-deleteActiveSource')" ng-mouseleave="hideTooltip(work.putCode.value+'-deleteActiveSource')">
                                            <span class="glyphicon glyphicon-trash"></span>
                                        </a>

                                        <div class="popover popover-tooltip top delete-activeSource-popover" ng-if="showElement[work.putCode.value+'-deleteActiveSource']">
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
                    <div ng-if="group.activePutCode != work.putCode.value" class="row source-line">
                        <div class="col-md-7 col-sm-7 col-xs-12">
                            <a ng-click="worksSrvc.switchWork(group, work.putCode.value);;showMozillaBadges(group.activePutCode);">                                
                                {{(work.sourceName == null || work.sourceName == '') ? work.source : work.sourceName }}
                            </a>
                        </div>
                        
                        <div class="col-md-3 col-sm-3 col-xs-10">
                             <#if !(isPublicProfile??)>
                                <span class="glyphicon glyphicon-check" ng-if="work.putCode.value == group.defaultWork.putCode.value"></span><span ng-if="work.putCode.value == group.defaultWork.putCode.value"> <@orcid.msg 'groups.common.preferred_source' /></span>
                                <a ng-click="worksSrvc.makeDefault(group, work.putCode.value); " ng-if="work.putCode.value != group.defaultWork.putCode.value">
                                   <span class="glyphicon glyphicon-unchecked"></span> <@orcid.msg 'groups.common.make_preferred' />
                                </a>
                            </#if>
                        </div>
                        <div class="col-md-2 col-sm-2 col-xs-2 trash-source">
                            <#if !(isPublicProfile??)>
                                <ul class="sources-actions">
                                    <#if RequestParameters['combine']??>
                                        <li ng-if="canBeCombined(work)">
                                            <a class="glyphicons git_pull_request" ng-click="showCombineMatches(group.defaultWork)" ng-mouseenter="showTooltip(work.putCode.value+'-combineInactiveDuplicates')" ng-mouseleave="hideTooltip(work.putCode.value+'-combineInactiveDuplicates')"></a>

                                            <div class="popover popover-tooltip top combine-inactiveDuplicates-popover" ng-if="showElement[work.putCode.value+'-combineInactiveDuplicates'] == true">
                                                <div class="arrow"></div>
                                                <div class="popover-content">
                                                    <@orcid.msg 'groups.common.combine_duplicates' />
                                                </div>
                                            </div>

                                        </li>
                                    </#if>
                                    <li> 
                                         <@orcid.editWorkIcon
                                            activity="work"
                                            click="openEditWork(work.putCode.value)"
                                            toolTipSuffix="editToolTipSourceActions"
                                            toolTipClass="popover popover-tooltip top edit-inactiveSource-popover"
                                         />
                                    </li>
                                    <li>
                                        <a ng-click="deleteWorkConfirm(work.putCode.value, false)" ng-mouseenter="showTooltip(work.putCode.value+'-deleteInactiveSource')" ng-mouseleave="hideTooltip(work.putCode.value+'-deleteInactiveSource')">
                                            <span class="glyphicon glyphicon-trash" title="<@orcid.msg 'freemarker.btnDelete'/> {{work.title.value}}"></span>
                                        </a>

                                        <div class="popover popover-tooltip top delete-inactiveSource-popover" ng-if="showElement[work.putCode.value+'-deleteInactiveSource'] == true">
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
                        
                        <div class="col-md-7 col-sm-7 col-xs-12">
                             <@orcid.msg 'groups.common.source'/>: {{(work.sourceName == null || work.sourceName == '') ? work.source : work.sourceName }}
                        </div>
                        
                        <div class="col-md-3 col-sm-3 col-xs-9">
                              <span class="glyphicon glyphicon-check"></span><span> <@orcid.msg 'groups.common.preferred_source' /></span> <span ng-hide="group.works.length == 1">(</span><a ng-click="showSources(group)" ng-hide="group.works.length == 1" ng-mouseenter="showTooltip(group.groupId+'-sources')" ng-mouseleave="hideTooltip(group.groupId+'-sources')"><@orcid.msg 'groups.common.of'/> {{group.works.length}}</a><span ng-hide="group.works.length == 1">)</span>

                              <div class="popover popover-tooltip top sources-popover" ng-if="showElement[group.groupId+'-sources']">
                                   <div class="arrow"></div>
                                   <div class="popover-content">
                                       <@orcid.msg 'groups.common.sources.show_other_sources' />
                                   </div>
                              </div>
                        </div>

                        <div class="col-md-2 col-sm-2 col-xs-3" ng-if="group.activePutCode == work.putCode.value">
                            <ul class="sources-options" ng-cloak>
                                <#if !(isPublicProfile??)>
                                    <#if RequestParameters['combine']??>
                                        <li ng-if="canBeCombined(work)">
                                            <a ng-click="showCombineMatches(group.defaultWork)" title="<@orcid.msg 'groups.common.combine_duplicates' />" ng-mouseenter="showTooltip(group.groupId+'-combineDuplicates')" ng-mouseleave="hideTooltip(group.groupId+'-combineDuplicates')">
                                                <span class="glyphicons git_pull_request"></span>
                                            </a>

                                            <div class="popover popover-tooltip top combine-duplicates-popover" ng-if="showElement[group.groupId+'-combineDuplicates']">
                                                <div class="arrow"></div>
                                                <div class="popover-content">
                                                    <@orcid.msg 'groups.common.combine_duplicates' />
                                                </div>
                                            </div>
                                        </li>
                                    </#if>

                                    <li>
                                        <@orcid.editWorkIcon
                                            activity="work"
                                            click="openEditWork(work.putCode.value)"
                                            toolTipSuffix="editToolTip"
                                            toolTipClass="popover popover-tooltip top edit-source-popover"
                                         />
                                    </li>

                                     <li ng-hide="editSources[group.groupId] == true || group.works.length == 1">
                                        <a ng-click="showSources(group)" ng-mouseenter="showTooltip(group.groupId+'-deleteGroup')" ng-mouseleave="hideTooltip(group.groupId+'-deleteGroup')">
                                            <span class="glyphicon glyphicon-trash"></span>
                                        </a>
                                        <div class="popover popover-tooltip top delete-group-popover" ng-if="showElement[group.groupId+'-deleteGroup']">
                                             <div class="arrow"></div>
                                            <div class="popover-content">
                                               <@orcid.msg 'groups.common.delete_this_source' />
                                            </div>
                                        </div>
                                     </li>

                                     <li ng-if="group.works.length == 1">
                                        <a ng-click="deleteWorkConfirm(group.activePutCode, false)" ng-mouseenter="showTooltip(group.groupId+'-deleteSource')" ng-mouseleave="hideTooltip(group.groupId+'-deleteSource')">
                                           <span class="glyphicon glyphicon-trash"></span>
                                        </a>
                                        <div class="popover popover-tooltip top delete-source-popover" ng-if="showElement[group.groupId+'-deleteSource']">
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
            </ul><!-- End of .sources-edit-list -->

        </div>
    </li>
</ul>
<button ng-cloak ng-show="worksSrvc.showLoadMore" ng-click="loadMore()" class="btn btn-primary">${springMacroRequestContext.getMessage("workspace.works.load_more")}</button>
<div ng-if="worksSrvc.loading" class="text-center" id="workSpinner">
    <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i><!-- Hidden with a CSS hack on IE 7 only -->
    <!--[if lt IE 8]>
        <img src="${staticCdn}/img/spin-big.gif" width="85" height ="85"/>
    <![endif]-->
</div>
<div ng-if="worksSrvc.loading == false && worksSrvc.groups.length == 0" class="" ng-cloak>
    <strong>
        <#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_works_body_list.Nopublicationsaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_works_body_list.havenotaddedanyworks")} 
            <a ng-if="noLinkFlag" ng-click="showWorkImportWizard()">${springMacroRequestContext.getMessage("workspace_works_body_list.addsomenow")}</a>
            <span ng-hide="noLinkFlag">${springMacroRequestContext.getMessage("workspace_works_body_list.addsomenow")}</span>
        </#if>
    </strong>
</div>