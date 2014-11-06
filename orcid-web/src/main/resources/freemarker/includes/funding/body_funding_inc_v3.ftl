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
	<li class="bottom-margin-small workspace-border-box card ng-scope" ng-repeat="group in fundingSrvc.groups | orderBy:sortPredicate:sortReverse">
		<div class="work-list-container">
			<ul class="sources-edit-list">
				<!-- HEADER -->
				<li ng-show="editSources[group.groupId] == true" ng-class="{'source-active' : editSources[group.groupId] == true}" ng-model="group.activities">						
                    <div class="sources-header">
                        <div class="row bottomBuffer">
                            <div class="col-md-9">
                               <span>
                                    <a ng-click="editSources[group.groupId] = false">
                                        <span class="glyphicon glyphicon-remove" ng-show="!bulkEditShow"></span> Hide additional sources
                                    </a>
                               </span>
                                <#if !(isPublicProfile??)>
                                    <span ng-show="editSources[group.groupId] == true">
                                        <a ng-click="deleteFundingConfirm(group.getActive().putCode.value, true)">                                        
                                            <span class="glyphicon glyphicon-trash" ng-show="!bulkEditShow"></span> Delete all
                                        </a>
                                    </span>
                                </#if>                  
                            </div>
                            <#if !(isPublicProfile??)>
                                <div class="col-md-3 col-sm-3 workspace-toolbar">
                                    <ul class="workspace-private-toolbar">
                                    	<li class=""><!-- Validate with ng-show for works is userIsSource(work) || (group.hasKeys() && !group.hasUserVersion()) -->
										    <a ng-click="openEditFunding(group.getActive())" class="toolbar-button edit-item-button">
										        <span class="glyphicon glyphicon-pencil edit-option-toolbar" title=""></span>
										    </a>
										</li>                                    
                                        <li>
                                            <@orcid.privacyToggle2  angularModel="group.getActive().visibility.visibility"
												questionClick="toggleClickPrivacyHelp(group.getActive().putCode.value)"
												clickedClassCheck="{'popover-help-container-show':privacyHelp[group.getActive().putCode.value]==true}" 
												publicClick="fundingSrvc.setGroupPrivacy(group.getActive().putCode.value, 'PUBLIC', $event)" 
							                	limitedClick="fundingSrvc.setGroupPrivacy(group.getActive().putCode.value, 'LIMITED', $event)" 
							                	privateClick="fundingSrvc.setGroupPrivacy(group.getActive().putCode.value, 'PRIVATE', $event)" />
                                        </li>
                                    </ul>
                                </div>
                            </#if>
                        </div>
                    
                        <div class="row">                      
                            <div class="col-md-4 col-sm-4 col-xs-4">
                                <div class="">
                                    <strong >Source</strong>
                                </div>
                            </div>                      
                            <div class="col-md-3 col-sm-3 col-xs-3">
                                <strong>Last Modified </strong>                         
                            </div>
                            
                            <div class="col-md-3 col-sm-3 col-xs-3">
                            	<#if !(isPublicProfile??)>
                                	<strong>Preferred</strong>
                                </#if>	
                            </div>
                            <div class="col-md-2 col-sm-2 col-xs-2 right">
                            	<#if !(isPublicProfile??)>
                                	<strong>Actions</strong>
                                </#if>	
                            </div>
                        </div>
                    </div>
                </li>
                
                
                <li ng-repeat="funding in group.activities" ng-show="group.activePutCode == funding.putCode.value || editSources[group.groupId] == true">
                    <!-- active row summary info -->
                    <div class="row" ng-show="group.activePutCode == funding.putCode.value">
                    	<div class="col-md-9 col-sm-12 col-xs-12">
                            <h3 class="workspace-title">
                                <h3 class="workspace-title">
								<strong ng-show="group.getActive().fundingTitle.title.value">{{group.getActive().fundingTitle.title.value}}:</strong>
								<span class="funding-name" ng-bind-html="group.getActive().fundingName.value"></span>					
							</h3>
				        	
                            <div class="info-detail">
                                <span class="funding-date" ng-show="group.getActive().startDate && !group.getActive().endDate">
									<span ng-show="group.getActive().startDate.year">{{group.getActive().startDate.year}}</span><span ng-show="group.getActive().startDate.month">-{{group.getActive().startDate.month}}</span>
									<#-- Do not move it to two lines -->						
							    	<@orcid.msg 'workspace_fundings.dateSeparator'/> <@orcid.msg 'workspace_fundings.present'/>
							    	<#-- ########################### -->
								</span>
								<span class="funding-date" ng-show="group.getActive().startDate && group.getActive().endDate">
									<span ng-show="group.getActive().startDate.year">{{group.getActive().startDate.year}}</span><span ng-show="group.getActive().startDate.month">-{{group.getActive().startDate.month}}</span>						
									<@orcid.msg 'workspace_fundings.dateSeparator'/>
									<span ng-show="group.getActive().endDate.year">{{group.getActive().endDate.year}}</span><span ng-show="group.getActive().endDate.month">-{{group.getActive().endDate.month}}</span>
								</span>
								<span class="funding-date" ng-show="!group.getActive().startDate && group.getActive().endDate">
								     <span ng-show="group.getActive().endDate.year">{{group.getActive().endDate.year}}</span><span ng-show="group.getActive().endDate.month">-{{group.getActive().endDate.month}}</span>
								</span>                  
                            </div>                            
                        </div>
                        
                        <#if !(isPublicProfile??)>
                            <div class="col-md-3 workspace-toolbar">
                                <ul class="workspace-private-toolbar" ng-hide="editSources[group.groupId] == true">
                                    <li><!-- Validate with ng-show for works is: !group.hasUserVersion() || userIsSource(work) -->
								 		<a href="" class="toolbar-button edit-item-button">
								 			<span class="glyphicon glyphicon-pencil edit-option-toolbar" title="" ng-click="openEditFunding(group.getActive())"></span>
								 		</a>	
								 	</li>
                                    <li>
                                        <@orcid.privacyToggle2  angularModel="group.getActive().visibility.visibility"
											questionClick="toggleClickPrivacyHelp(group.getActive().putCode.value)"
											clickedClassCheck="{'popover-help-container-show':privacyHelp[group.getActive().putCode.value]==true}" 
											publicClick="fundingSrvc.setGroupPrivacy(group.getActive().putCode.value, 'PUBLIC', $event)" 
						                	limitedClick="fundingSrvc.setGroupPrivacy(group.getActive().putCode.value, 'LIMITED', $event)" 
						                	privateClick="fundingSrvc.setGroupPrivacy(group.getActive().putCode.value, 'PRIVATE', $event)" />
                                    </li>
                                </ul>
                            </div>
                        </#if>
                    </div>
                    
                    <!-- Active Row Identifiers / URL / Validations / Versions -->
                    <div class="row" ng-show="group.activePutCode == funding.putCode.value">
                         <div class="col-md-12 col-sm-12">
                             <ul class="id-details">         
                                 <li>
                                     <span ng-repeat='ei in group.getActive().externalIdentifiers'>							
										<span ng-bind-html='ei | externalIdentifierHtml:$first:$last:group.getActive().externalIdentifiers.length'>
										</span>
									</span>
                                 </li>
                             </ul>
                         </div>
                     </div> 
                     
                     <!-- more info -->
                     <#include "funding_more_info_inc_v3.ftl"/>
                     
                     <!-- active row  source display -->
                      <div class="row" ng-show="group.activePutCode == funding.putCode.value">
                      	<div class="col-md-4">
                      		<strong >Source:</strong> {{group.getActive().sourceName}}
                      	</div>
                      	<div class="col-md-3" ng-show="editSources[group.groupId] == true">
                      		<div ng-show="editSources[group.groupId] == true">
                      			{{group.getActive().lastModified | ajaxFormDateToISO8601}}
                      		</div>
                      	</div>              
                      	<div class="col-md-3" ng-show="editSources[group.groupId] == true">
                     		<#if !(isPublicProfile??)>
								<div ng-show="editSources[group.groupId] == true">
							        <span class="glyphicon glyphicon-check ng-hide" ng-show="funding.putCode.value == group.defaultPutCode"></span> 
							        <a ng-click="fundingSrvc.makeDefault(group, funding.putCode.value);" ng-show="funding.putCode.value != group.defaultPutCode" class="">
							         	<span class="glyphicon glyphicon-unchecked"></span> Make Preferred
							        </a>
								</div>    
							</#if>
                      	</div>
                      	<div class="col-md-2 trash-source">
                      		<div ng-show="editSources[group.groupId] == true">
					        <#if !(isPublicProfile??)>
					        	<ul class="sources-actions">					        		
					        		<li>
                                        <a ng-click="openEditFunding(group.getActive())" class="">
                                            <span class="glyphicon glyphicon-pencil"></span>
                                        </a>
                                    </li>
					        		<li>
					        			<a ng-click="deleteFundingConfirm(group.getActive().putCode.value, false)">
						            	   <span class="glyphicon glyphicon-trash"></span>
						                </a>
					        		</li>
					        	</ul>
							</#if>
						</div>
                      	</div>
                    </div>
                    
                    <!-- not active row && edit sources -->
                    <div ng-show="group.activePutCode != funding.putCode.value" class="row">                       
                        <div class="col-md-4 col-sm-4 col-xs-4">
                                <a ng-click="group.activePutCode = funding.putCode.value;">
                                {{group.getActive().sourceName}}
                            </a> 
                        </div>
                        <div class="col-md-3 col-sm-3 col-xs-3">
                            {{group.getActive().lastModified | ajaxFormDateToISO8601}}
                        </div>
                        <div class="col-md-3 col-sm-3 col-xs-5">
                        	 <#if !(isPublicProfile??)>
	                            <span class="glyphicon glyphicon-check" ng-show="funding.putCode.value == group.defaultPutCode"></span> 
	                            <a ng-click="fundingSrvc.makeDefault(group, funding.putCode.value); " ng-show="funding.putCode.value != group.defaultPutCode">
	                               <span class="glyphicon glyphicon-unchecked"></span> Make Preferred
	                            </a>
                            </#if> 
                        </div>
                        
                        
                        <div class="col-md-2 col-sm-2 col-xs-12 trash-source">
                            <#if !(isPublicProfile??)>
                                <ul class="sources-actions">
                                    <li> <!-- Validate with ng-show for works is: !group.hasUserVersion() || userIsSource(work) -->
                                        <a ng-click="openEditFunding(group.getActive())">
                                            <span class="glyphicon glyphicon-pencil"></span>
                                        </a>
                                    </li>
                                    <li>
                                        <a ng-click="deleteFundingConfirm(group.getActive().putCode.value, false)">
                                            <span class="glyphicon glyphicon-trash"></span>
                                        </a>
                                    </li>
                                </ul>
                            </#if>
                        </div>
                        
                                              
                    </div>
                    <div class="row">
                    	<div class="col-md-12" ng-show="group.activePutCode == funding.putCode.value">
                    		<ul class="sources-options" ng-cloak>                                	
							    <li ng-hide="group.activitiesCount == 1 || editSources[group.groupId] == true">                         
							        <span class="view-sources-details">
							            <a ng-click="editSources[group.groupId] = !editSources[group.groupId]">View <span class="badge">{{group.activitiesCount - 1 }}</span> additional source<span ng-show="group.activitiesCount > 2">s</span></a>                               
							        </span>
							        <#if !(isPublicProfile??)>
							          <a ng-click="editSources[group.groupId] = !editSources[group.groupId]" ng-show="!bulkEditShow">
							             <span class="glyphicon glyphicon-trash grey"></span>
									</a>
							        </#if>
							    </li>
							    <#if !(isPublicProfile??)>
							         <li ng-show="group.activitiesCount == 1">
							            <a ng-click="deleteFundingConfirm(group.getActive().putCode.value, false)">
							               <span class="glyphicon glyphicon-trash grey" ng-show="!bulkEditShow"></span>
							           </a>
							         </li>
							      </#if>
							    <li class="show-more-info-tab-container">
							        <span class="show-more-info-tab work-tab">
							        	<!-- The code for getting this working needs to be added in orcidAngular.js -->
							            <a ng-show="!moreInfo[group.groupId]" ng-click="showDetailsMouseClick(group,$event);"><span class="glyphicon glyphicon-chevron-down"></span><@orcid.msg 'manage.developer_tools.show_details'/></a>                                 
							            <a ng-show="moreInfo[group.groupId]" ng-click="showDetailsMouseClick(group,$event);"><span class="glyphicon glyphicon-chevron-up"></span><@orcid.msg 'manage.developer_tools.hide_details'/></a>
							        </span>                         
							    </li>                               
							</ul>
                    	
                    	</div>
                    </div>                
                </li><!-- End line -->
       		</ul>     	        
	 	</div>
	</li>
</ul>

<div ng-show="fundingSrvc.loading == true;" class="text-center">
    <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
    <!--[if lt IE 8]>    
    	<img src="${staticCdn}/img/spin-big.gif" width="85" height ="85"/>
    <![endif]-->
</div>
<div ng-show="fundingSrvc.loading == false && fundingSrvc.groups.length == 0" class="" ng-cloak>
    <strong><#if (publicProfile)?? && publicProfile == true><@orcid.msg 'workspace_fundings_body_list.nograntaddedyet' /><#else><@orcid.msg 'workspace_fundings.havenotaddaffiliation' /><a ng-click="addFundingModal()"> <@orcid.msg 'workspace_fundings_body_list.addsomenow'/></a></#if></strong>
</div>

