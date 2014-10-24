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
<ul ng-hide="!worksSrvc.groups.length" class="workspace-publications bottom-margin-medium" id="body-work-list" ng-cloak>	
    <li class="bottom-margin-small workspace-border-box card" ng-repeat="group in worksSrvc.groups | orderBy:sortPredicate:sortReverse">    	
    	<div class="work-list-container">
			<ul class="sources-edit-list">				
				<li ng-repeat="work in group.activities" ng-show="group.activePutCode == work.putCode.value || editSources[group.groupId] == true" ng-class="{'source-active' : group.activePutCode == work.putCode.value && editSources[group.groupId] == true}">
					<!-- active row summary info -->
					<div class="row" ng-show="group.activePutCode == work.putCode.value">
						<div class="col-md-8 col-sm-8 col-xs-12">
					    	<h3 class="workspace-title">
				        		<strong ng-bind="work.workTitle.title.value"></strong><span class="work-subtitle" ng-show="work.workTitle.subtitle.value" ng-bind="':&nbsp;'.concat(work.workTitle.subtitle.value)"></span>		        			        	
				        	</h3>
				        	<div ng-show="bulkEditShow == true" class="bulk-edit-input hidden-lg hidden-md hidden-sm pull-right">
				        		<input type="checkbox" ng-model="bulkEditMap[work.putCode.value]"></input>
				        	</div>
				        	<div class="info-detail">
				        		<span ng-show="work.publicationDate.year">{{work.publicationDate.year}}</span><span ng-show="work.publicationDate.month">-{{work.publicationDate.month}}</span><span ng-show="work.publicationDate.year"> | </span> <span class="uppercase">{{work.workType.value}}</span>		        	
				        	</div>		                	        
		        		</div>
		        	
			        	<!-- Settings -->
				        <div class="col-md-4 col-sm-4 col-xs-12 workspace-toolbar">	        	
				        	<#if !(isPublicProfile??)>
				        		<!-- Privacy bar -->
								<ul class="workspace-private-toolbar">
									<li ng-show="bulkEditShow" class="hidden-xs bulk-checkbox-item">								
					        			<input type="checkbox" ng-model="bulkEditMap[work.putCode.value]" class="bulk-edit-input"></input>			        										
									</li>							
								 	<li>
								 		<a class="toolbar-button edit-item-button" ng-click="openEditWork(work.putCode.value)">
								 			<span class="glyphicon glyphicon-pencil edit-option-toolbar" title=""></span>
								 		</a>
								 	</li>
									<li>
										<@orcid.privacyToggle2 angularModel="work.visibility" 
										    questionClick="toggleClickPrivacyHelp(work.putCode.value)"
										    clickedClassCheck="{'popover-help-container-show':privacyHelp[work.putCode.value]==true}"
											publicClick="worksSrvc.setGroupPrivacy(work.putCode.value, 'PUBLIC', $event)" 
						                	limitedClick="worksSrvc.setGroupPrivacy(work.putCode.value, 'LIMITED', $event)" 
						                	privateClick="worksSrvc.setGroupPrivacy(work.putCode.value, 'PRIVATE', $event)"/>
					                </li>
					             </ul>
					        </#if>
					        <#if RequestParameters['combine']??>
					        	<div ng-show="canBeCombined(work)">
					            	<a ng-click="showCombineMatches(group.getDefault())">combined duplicates</a>
					        	</div>
					        </#if>
							</div>
				        </div>
				        	
				        <!-- Active Row Identifiers / URL / Validations / Versions -->
						<div class="row" ng-show="work.workExternalIdentifiers.length > 0 && group.activePutCode == work.putCode.value"">
							<div class="col-md-12 col-sm-12">
								<ul class="id-details">			
									<li>
										<span ng-repeat='ie in work.workExternalIdentifiers'><span
										ng-bind-html='ie | workExternalIdentifierHtml:$first:$last:work.workExternalIdentifiers.length'></span>
									   </span>
									</li>
									<li ng-show="work.url.value"><strong>URL:</strong> <a href="{{work.url.value | urlWithHttp}}" target="_blank">{{work.url.value}}</a></li>
								</ul>
							</div>
						</div> 
							
						<!-- more info -->
						<#include "work_more_info_inc_v3.ftl"/>
							
						<!-- active row  source display -->
						<div class="row" ng-show="group.activePutCode == work.putCode.value">					 
							
							<div class="col-md-4">
								<span>
									<strong >Source:</strong>{{work.workSourceName.value}}
								</span>						
							</div>
						
							<div class="col-md-8">						
								<ul class="sources-options" ng-cloak>
									<li ng-hide="group.activitiesCount == 1 || editSources[group.groupId] == true">							
										<span class="view-sources-details">
										 	<a ng-click="editSources[group.groupId] = !editSources[group.groupId]">View <span class="badge">{{group.activitiesCount - 1 }}</span> additional source<span ng-show="group.activitiesCount > 2">s</span></a>							 	
										</span>
										<a ng-click="editSources[group.groupId] = !editSources[group.groupId]" ng-show="!bulkEditShow">
						            	   <span class="glyphicon glyphicon-trash grey"></span>
						        		</a>
									</li>
									<#if !(isPublicProfile??)>
										<li ng-show="group.activitiesCount == 1">
											<a ng-click="deleteWorkConfirm(group.getActive().putCode.value, false)">
							            	   <span class="glyphicon glyphicon-trash grey" ng-show="!bulkEditShow"></span>
							               </a>
										</li>
								        <li ng-show="editSources[group.groupId] == true">
								            <a ng-click="deleteWorkConfirm(group.getActive().putCode.value, true)">
								                <span class="glyphicon glyphicon-trash" ng-show="!bulkEditShow"></span> Delete all
								            </a>
								        </li>
								    </#if>
							        <li ng-show="editSources[group.groupId] == true">
							            <a ng-click="editSources[group.groupId] = false">
							                <span class="glyphicon glyphicon-remove" ng-show="!bulkEditShow"></span> Hide additional sources
							            </a>
							        </li>
							        <li class="show-more-info-tab-container">
								        <span class="show-more-info-tab work-tab">			
											<a href="" ng-show="!moreInfo[group.getActive().putCode.value]" ng-click="showDetailsMouseClick(group.getActive().putCode.value,$event);"><span class="glyphicon glyphicon-chevron-down"></span><@orcid.msg 'manage.developer_tools.show_details'/></a>									
											<a href="" ng-show="moreInfo[group.getActive().putCode.value]" ng-click="showDetailsMouseClick(group.getActive().putCode.value,$event);"><span class="glyphicon glyphicon-chevron-up"></span><@orcid.msg 'manage.developer_tools.hide_details'/></a>
										</span>							
							        </li>                               
							    </ul>
							</div>
						</div>	
					
					
					<!-- not active row && edit sources -->
					<div ng-show="group.activePutCode != work.putCode.value" class="row">						
						<div class="col-md-4 col-sm-4 col-xs-4">
								<a ng-click="group.activePutCode = work.putCode.value; hideLastDetails()">
				           		{{work.workSourceName.value}}
				           	</a> 
						</div>
						<div class="col-md-4 col-sm-3 col-xs-3">
							{{work.lastModified | ajaxFormDateToISO8601}}
						</div>
						<div class="col-md-3 col-sm-3 col-xs-5">
							<span class="glyphicon glyphicon-check" ng-show="work.putCode.value == group.defaultPutCode"></span> 
				        	<a ng-click="worksSrvc.makeDefault(group, work.putCode.value); " ng-show="work.putCode.value != group.defaultPutCode">
				           	 <span class="glyphicon glyphicon-unchecked"></span> Make Preferred
			             	</a>
						</div>
						<div class="col-md-1 col-sm-2 col-xs-12 trash-source">
							<#if !(isPublicProfile??)>
								<a ng-click="deleteWorkConfirm(work.putCode.value, false)">
						          	   <span class="glyphicon glyphicon-trash"></span>
					            </a>
				            </#if>    
						</div>						
					</div>
					
					
				</li>
			</ul>				
		</div>
	</li><!-- bottom-margin-small -->	
</ul>
<div ng-show="worksSrvc.loading == true" class="text-center" id="workSpinner">
	<i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i><!-- Hidden with a CSS hack on IE 7 only -->
    <!--[if lt IE 8]>    
    	<img src="${staticCdn}/img/spin-big.gif" width="85" height ="85"/>
    <![endif]-->
</div>
<div ng-show="worksSrvc.loading == false && worksSrvc.groups.length == 0" class="" ng-cloak>
    <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_works_body_list.Nopublicationsaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_works_body_list.havenotaddedanyworks")} <a ng-click="addWorkModal()">${springMacroRequestContext.getMessage("workspace_works_body_list.addsomenow")}</a></#if></strong>
</div>