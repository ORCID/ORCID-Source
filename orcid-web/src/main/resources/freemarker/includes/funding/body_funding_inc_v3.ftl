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
<ul ng-hide="!fundingSrvc.groups.length" class="workspace-fundings workspace-body-list bottom-margin-medium" ng-cloak>
    <li class="bottom-margin-small workspace-border-box card ng-scope" ng-repeat="group in fundingSrvc.groups | orderBy:sortState.predicate:sortState.reverse">
        <div class="work-list-container">
            <ul class="sources-edit-list">
                <!-- Header -->
                <li ng-if="editSources[group.groupId] == true" class="source-header" ng-class="{'source-active' : editSources[group.groupId] == true}" ng-model="group.activities">
                    <div class="sources-header">
                        <div class="row">
                            <div class="col-md-7 col-sm-7 col-xs-7">
                                <@orcid.msg 'groups.common.sources' /> <span class="hide-sources" ng-click="hideSources(group)"><@orcid.msg 'groups.common.close_sources' /></span>
                            </div>                            
                            <div class="col-md-2 col-sm-2 col-xs-2">                                
                                <@orcid.msgCapFirst 'groups.common.preferred' />
                            </div>                            
                            <div class="col-md-3 col-sm-3 col-xs-3 right">
                                <#if !(isPublicProfile??)>
                                    <div class="workspace-toolbar">
                                        <ul class="workspace-private-toolbar">
                                             <li class="works-details" ng-if="editSources[group.groupId]">                                        
                                                <a ng-click="showDetailsMouseClick(group.groupId,$event);" ng-mouseenter="showTooltip(group.groupId+'-showHideDetails')" ng-mouseleave="hideTooltip(group.groupId+'-showHideDetails')">
                                                    <span ng-class="(moreInfo[group.groupId] == true) ? 'glyphicons collapse_top' : 'glyphicons expand'">
                                                    </span>
                                                </a>                                        
                                                <div class="popover popover-tooltip top show-hide-details-popover ng-hide" ng-if="showElement[group.groupId+'-showHideDetails']">
                                                     <div class="arrow"></div>
                                                    <div class="popover-content">
                                                        <span ng-if="moreInfo[group.groupId] == false || moreInfo[group.groupId] == null" class=""><@orcid.msg 'common.details.show_details'/></span>                                    
                                                        <span ng-if="moreInfo[group.groupId] == true" class="ng-hide">Hide Details</span>
                                                    </div>
                                                </div>                                        
                                            </li>
    
                                            <li>
                                                <@orcid.privacyToggle2  angularModel="group.getActive().visibility.visibility"
                                                    questionClick="toggleClickPrivacyHelp(group.getActive().putCode.value)"
                                                    clickedClassCheck="{'popover-help-container-show':privacyHelp[group.getActive().putCode.value]==true}"
                                                    publicClick="fundingSrvc.setGroupPrivacy(group.getActive().putCode.value, 'PUBLIC', $event)"
                                                    limitedClick="fundingSrvc.setGroupPrivacy(group.getActive().putCode.value, 'LIMITED', $event)"
                                                    privateClick="fundingSrvc.setGroupPrivacy(group.getActive().putCode.value, 'PRIVATE', $event)"
                                                    />
                                            </li>
                                        </ul>
                                    </div>
                                </#if>
                             </div>
                        </div>
                    </div>
                </li><!--  End of header -->


                <li ng-repeat="funding in group.activities" ng-if="group.activePutCode == funding.putCode.value || editSources[group.groupId]" funding-put-code="{{funding.putCode.value}}">
                    <!-- active row summary info -->
                    <div class="row" ng-if="group.activePutCode == funding.putCode.value">
                        <div class="col-md-9 col-sm-9 col-xs-7">
                            <h3 class="workspace-title">                                
                               <span ng-if="group.getActive().fundingTitle.title.value" ng-bind="group.getActive().fundingTitle.title.value"></span>                               
                            </h3>
                            <div class="clear-fix left">
                                <span ng-if="group.getActive().fundingName.value"><span ng-bind="group.getActive().fundingName.value"></span></span> (<span ng-if="group.getActive().city.value"><span ng-bind="group.getActive().city.value"></span></span><span ng-if="group.getActive().region.value">, <span ng-bind="group.getActive().region.value"></span></span><span ng-if="group.getActive().countryForDisplay">, <span ng-bind="group.getActive().countryForDisplay"></span></span>)
                            </div>  
                            <div class="info-detail">
                                <!-- Funding date -->
                                <span class="funding-date" ng-if="group.getActive().startDate && !group.getActive().endDate">
                                    <span ng-if="group.getActive().startDate.year" ng-bind="group.getActive().startDate.year"></span><span ng-if="group.getActive().startDate.month">-</span><span ng-if="group.getActive().startDate.month" ng-bind="group.getActive().startDate.month"></span>
                                    <#-- Do not move it to two lines -->
                                    <@orcid.msg 'workspace_fundings.dateSeparator'/> <@orcid.msg 'workspace_fundings.present'/>
                                    <#-- ########################### -->
                                </span>
                                <span class="funding-date" ng-if="group.getActive().startDate && group.getActive().endDate">
                                    <span ng-if="group.getActive().startDate.year" ng-bind="group.getActive().startDate.year"></span><span ng-if="group.getActive().startDate.month">-</span><span ng-if="group.getActive().startDate.month" ng-bind="group.getActive().startDate.month"></span>
                                    <@orcid.msg 'workspace_fundings.dateSeparator'/>
                                    <span ng-if="group.getActive().endDate.year" ng-bind="group.getActive().endDate.year"></span><span ng-if="group.getActive().endDate.month">-</span><span ng-if="group.getActive().endDate.month" ng-bind="group.getActive().endDate.month"></span>
                                </span>
                                <span class="funding-date" ng-if="!group.getActive().startDate && group.getActive().endDate">
                                     <span ng-if="group.getActive().endDate.year" ng-bind="group.getActive().endDate.year"></span><span ng-if="group.getActive().endDate.month">-</span><span ng-if="group.getActive().endDate.month" ng-bind="group.getActive().endDate.month"></span>
                                </span>                                
                                <!-- Funding type -->
                                <span ng-if="(group.getActive().startDate || group.getActive().endDate) && group.getActive().fundingType.value">|</span> <span ng-bind="group.getActive().fundingTypeForDisplay" class="capitalize"></span>
                            </div>                            
                        </div>


                        <div class="col-md-3 col-sm-3 col-xs-5 workspace-toolbar">
                            <ul class="workspace-private-toolbar" ng-hide="editSources[group.groupId] == true">
                                <#if !(isPublicProfile??)>
                                    <!-- Bulk edit tool / for further implementation
                                    <li ng-if="bulkEditShow == true" class="hidden-xs bulk-checkbox-item">                                
                                            <input type="checkbox" ng-model="bulkEditMap[funding.putCode.value]" class="bulk-edit-input ng-pristine ng-valid pull-right">                                                            
                                    </li>
                                    -->
                                </#if>
                                <!-- Show/Hide Details -->
                                <li class="works-details" ng-hide="editSources[group.groupId] == true">                                        
                                    <a ng-click="showDetailsMouseClick(group.groupId,$event);" ng-mouseenter="showTooltip(group.groupId+'-showHideDetails')" ng-mouseleave="hideTooltip(group.groupId+'-showHideDetails')">
                                        <span ng-class="(moreInfo[group.groupId] == true) ? 'glyphicons collapse_top' : 'glyphicons expand'">
                                        </span>
                                    </a>                                        
                                    <div class="popover popover-tooltip top show-hide-details-popover" ng-if="showElement[group.groupId+'-showHideDetails'] == true">
                                        <div class="arrow"></div>
                                        <div class="popover-content">
                                            <span ng-if="moreInfo[group.groupId] == false || moreInfo[group.groupId] == null"><@orcid.msg 'common.details.show_details'/></span>                                    
                                            <span ng-if="moreInfo[group.groupId]">Hide Details</span>
                                        </div>
                                    </div>                                        
                                </li>
                                <#if !(isPublicProfile??)>
                                    <li>
                                        <@orcid.privacyToggle2  angularModel="group.getActive().visibility.visibility"
                                            questionClick="toggleClickPrivacyHelp(group.getActive().putCode.value)"
                                            clickedClassCheck="{'popover-help-container-show':privacyHelp[group.getActive().putCode.value]==true}"
                                            publicClick="fundingSrvc.setGroupPrivacy(group.getActive().putCode.value, 'PUBLIC', $event)"
                                            limitedClick="fundingSrvc.setGroupPrivacy(group.getActive().putCode.value, 'LIMITED', $event)"
                                            privateClick="fundingSrvc.setGroupPrivacy(group.getActive().putCode.value, 'PRIVATE', $event)" />
                                    </li>
                                </#if>  
                            </ul>
                            <#if !(isPublicProfile??)>
                                <div ng-if="!group.consistentVis() && !editSources[group.groupId]" class="vis-issue">
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
                    <div class="row" ng-if="group.activePutCode == funding.putCode.value">
                        <div class="col-md-12 col-sm-12 bottomBuffer">
                            <ul class="id-details">
                                <li>
                                    <span ng-repeat='ei in group.getActive().externalIdentifiers'>
                                        <span bind-html-compile='ei | externalIdentifierHtml:$first:$last:group.getActive().externalIdentifiers.length:group.getActive().fundingType:moreInfo[group.groupId]' class="url-popover"> 
                                        </span>
                                    </span>
                                </li>
                                <li ng-if="group.getActive().url.value" class="url-popover">
                                    <@orcid.msg 'manual_funding_form_contents.label_url'/>: <a href="{{group.getActive().url.value | urlProtocol}}" ng-class="{'truncate-anchor' : moreInfo[group.groupId] == false || moreInfo[group.groupId] == null}" ng-bind="group.getActive().url.value" target="_blank" ng-mouseenter='showURLPopOver(funding.putCode.value + "-alternate")' ng-mouseleave='hideURLPopOver(funding.putCode.value + "-alternate")'></a>                                       
                                    <div class="popover-pos">
                                        <div class="popover-help-container">
                                           <div class="popover bottom" ng-class="{'block' : displayURLPopOver[funding.putCode.value + '-alternate'] == true}">
                                                <div class="arrow"></div>
                                                <div class="popover-content">
                                                    <a href="{{group.getActive().url.value}}" target="_blank" >{{group.getActive().url.value}}</a>
                                                </div>                
                                            </div>                              
                                        </div>
                                    </div>
                                </li>
                            </ul>
                        </div>
                    </div>

                    <!-- more info -->
                    <#include "funding_more_info_inc_v3.ftl"/>

                    <!-- active row source display -->
                    <div class="row source-line" ng-if="group.activePutCode == funding.putCode.value">
                        <div class="col-md-7 col-sm-7 col-xs-12" ng-if="editSources[group.groupId] == true">                              
                            {{(group.getActive().sourceName == null || group.getActive().sourceName == '') ? group.getActive().source : group.getActive().sourceName}}
                        </div>                          
                        <div class="col-md-3 col-sm-3 col-xs-6" ng-if="editSources[group.groupId] == true">

                            <span class="glyphicon glyphicon-check" ng-if="funding.putCode.value == group.defaultPutCode"></span><span ng-if="funding.putCode.value == group.defaultPutCode"> <@orcid.msg 'groups.common.preferred_source' /></span>
                            <#if !(isPublicProfile??)>
                                <div ng-if="editSources[group.groupId]">
                                    <a ng-click="fundingSrvc.makeDefault(group, funding.putCode.value);" ng-if="funding.putCode.value != group.defaultPutCode" class="">
                                        <span class="glyphicon glyphicon-unchecked"></span> <@orcid.msg 'groups.common.make_preferred' />
                                    </a>
                                </div>
                            </#if>
                        </div>
                        <div class="col-md-2 col-sm-2  col-xs-6 trash-source" ng-if="editSources[group.groupId]">

                            <#if !(isPublicProfile??)>
                                <ul class="sources-actions">
                                    <li>
                                        <@orcid.editActivityIcon
                                    activity="funding"
                                    click="openEditFunding(funding.putCode.value)"
                                    toolTipSuffix="editFundingToolSourceActions"
                                    toolTipClass="popover popover-tooltip top edit-activeSource-popover"
                                    />
                                    </li>
                                    <li>
                                        <a ng-click="deleteFundingConfirm(group.getActive().putCode.value, false)"  ng-mouseenter="showTooltip(group.groupId+'-deleteActiveSource')" ng-mouseleave="hideTooltip(group.groupId+'-deleteActiveSource')">
                                            <span class="glyphicon glyphicon-trash"></span>
                                        </a>

                                        <div class="popover popover-tooltip top delete-activeSource-popover" ng-if="showElement[group.groupId+'-deleteActiveSource']">
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

                    <!-- not active row && edit sources -->
                    <div ng-if="group.activePutCode != funding.putCode.value" class="row source-line">
                        <div class="col-md-7 col-sm-7 col-xs-12">
                                <a ng-click="group.activePutCode = funding.putCode.value;">                                
                                {{(funding.sourceName == null || funding.sourceName == '') ? funding.source : funding.sourceName}}
                            </a>
                        </div>                        
                        <div class="col-md-3 col-sm-3 col-xs-6">
                             <#if !(isPublicProfile??)>
                                <span class="glyphicon glyphicon-check" ng-if="funding.putCode.value == group.defaultPutCode"></span><span ng-if="funding.putCode.value == group.defaultPutCode"> <@orcid.msg 'groups.common.preferred_source' /></span>
                                <a ng-click="fundingSrvc.makeDefault(group, funding.putCode.value);" ng-if="funding.putCode.value != group.defaultPutCode">
                                   <span class="glyphicon glyphicon-unchecked"></span> <@orcid.msg 'groups.common.make_preferred' />
                                </a>
                            </#if>
                        </div>


                        <div class="col-md-2 col-sm-2 col-xs-6 trash-source">
                            <#if !(isPublicProfile??)>
                                <ul class="sources-actions">
                                    <li> 
                                        <@orcid.editActivityIcon
                                            activity="funding"
                                            click="openEditFunding(funding.putCode.value)"
                                            toolTipSuffix="editFundingToolSourceActions"
                                            toolTipClass="popover popover-tooltip top edit-inactiveSource-popover"
                                        />
                                    </li>
                                    <li>
                                        <a ng-click="deleteFundingConfirm(group.getActive().putCode.value, false)" ng-mouseenter="showTooltip(funding.putCode.value+'-deleteInactiveSource')" ng-mouseleave="hideTooltip(funding.putCode.value+'-deleteInactiveSource')">
                                            <span class="glyphicon glyphicon-trash"></span>
                                        </a>
                                        <div class="popover popover-tooltip top delete-inactiveSource-popover" ng-if="showElement[funding.putCode.value+'-deleteInactiveSource']">
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

                    <!-- Bottom row -->

                    <div class="row source-line" ng-hide="editSources[group.groupId] == true">
                        <div class="col-md-7 col-sm-7 col-xs-12">
                              <@orcid.msg 'groups.common.source'/>: {{(funding.sourceName == null || funding.sourceName == '') ? funding.source : funding.sourceName}}
                        </div>                          
                        <div class="col-md-3 col-sm-3 col-xs-6" ng-if="group.activePutCode == funding.putCode.value">
                            <span class="glyphicon glyphicon-check"></span><span> <@orcid.msg 'groups.common.preferred_source' /></span> <span ng-hide="group.activitiesCount == 1">(</span><a ng-click="showSources(group)" ng-hide="group.activitiesCount == 1" ng-mouseenter="showTooltip(group.groupId+'-sources')" ng-mouseleave="hideTooltip(group.groupId+'-sources')"><@orcid.msg 'groups.common.of'/> {{group.activitiesCount}}</a><span ng-hide="group.activitiesCount == 1">)</span>
                            
                            <div class="popover popover-tooltip top sources-popover" ng-if="showElement[group.groupId+'-sources']">
                                <div class="arrow"></div>
                                <div class="popover-content">
                                    <@orcid.msg 'groups.common.sources.show_other_sources' />                                
                                </div>
                            </div>
                        </div>
                        <div class="col-md-2 col-sm-2 col-xs-6">
                            <ul class="sources-options" ng-cloak>
                                <#if !(isPublicProfile??)>
                                    <li>
                                         <@orcid.editActivityIcon
                                            activity="funding"
                                            click="openEditFunding(funding.putCode.value)"
                                            toolTipSuffix="editFundingToolTipSources"
                                            toolTipClass="popover popover-tooltip top edit-source-popover"
                                         />
                                    </li>
                                    <li ng-hide="group.activitiesCount == 1 || editSources[group.groupId] == true">
    
                                        <a ng-click="showSources(group)" ng-mouseenter="showTooltip(group.groupId+'-deleteGroup')" ng-mouseleave="hideTooltip(group.groupId+'-deleteGroup')">
                                             <span class="glyphicon glyphicon-trash"></span>
                                        </a>
                                        <div class="popover popover-tooltip top delete-source-popover" ng-if="showElement[group.groupId+'-deleteGroup']">
                                             <div class="arrow"></div>
                                            <div class="popover-content">
                                                 <@orcid.msg 'groups.common.delete_this_source' />                                
                                            </div>
                                        </div>  
                                    </li>
                                    <li ng-if="group.activitiesCount == 1">
                                       <a id="delete-funding_{{group.getActive().putCode.value}}" ng-click="deleteFundingConfirm(group.getActive().putCode.value, false)" ng-mouseenter="showTooltip(group.groupId+'-deleteSource')" ng-mouseleave="hideTooltip(group.groupId+'-deleteSource')">
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
                </li><!-- End line -->
            </ul>
        </div>
    </li>
</ul>

<div ng-if="fundingSrvc.loading" class="text-center">
    <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
    <!--[if lt IE 8]>
        <img src="${staticCdn}/img/spin-big.gif" width="85" height ="85"/>
    <![endif]-->
</div>
<div ng-if="fundingSrvc.loading == false && fundingSrvc.groups.length == 0" class="" ng-cloak>
    <strong>
        <#if (publicProfile)?? && publicProfile == true><@orcid.msg 'workspace_fundings_body_list.nograntaddedyet' /><#else><@orcid.msg 'workspace_fundings.havenotaddaffiliation' />
            <#if fundingImportWizards?has_content>
                <a ng-click="showTemplateInModal('import-funding-modal')"> <@orcid.msg 'workspace_fundings_body_list.addsomenow'/></a>
            <#else>
                <span><@orcid.msg 'workspace_fundings_body_list.addsomenow'/></span>
            </#if>
        </#if>
    </strong>
</div>

