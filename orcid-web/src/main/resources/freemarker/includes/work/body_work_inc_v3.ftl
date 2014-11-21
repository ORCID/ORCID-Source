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
    <li class="bottom-margin-small workspace-border-box card" ng-repeat="group in worksSrvc.groups | orderBy:sortState.predicate:sortState.reverse">        
        <div class="work-list-container">
            <ul class="sources-edit-list">
                
                
                <!-- Header -->
                <li ng-show="editSources[group.groupId] == true" class="source-header" ng-class="{'source-active' : editSources[group.groupId] == true}" ng-model="group.activities">
                    <div class="sources-header">
                        <div class="row">                      
                            <div class="col-md-4 col-sm-4 col-xs-4">
                                <div class="">
                                    Sources <span class="hide-sources" ng-click="hideSources(group)">Close sources</span>
                                </div>
                            </div>
                            <div class="col-md-3 col-sm-3 col-xs-3">
                                Created                         
                            </div>
                            
                            <div class="col-md-2 col-sm-3 col-xs-3">                            	
                                Preferred
                            </div>
                            <div class="col-md-3 col-sm-2 col-xs-2 right">                            	
                                	<div class="workspace-toolbar">                                	
	                                    <ul class="workspace-private-toolbar">
	                                    	<#if !(isPublicProfile??)>   
		                                    	<li ng-show="bulkEditShow">
		                                    		<input type="checkbox" ng-model="bulkEditMap[group.getActive().putCode.value]" class="bulk-edit-input-header ng-valid ng-dirty">
		                                    	</li>
	                                    	</#if>
	                                    	<li class="works-details">                                		
		                                		<a ng-click="showDetailsMouseClick(group,$event);" ng-mouseenter="showTooltip(group.groupId+'-showHideDetails')" ng-mouseleave="hideTooltip(group.groupId+'-showHideDetails')">
		                                			<span ng-class="(moreInfo[group.groupId] == true) ? 'glyphicons book' : 'glyphicons book_open'">
		                                			</span>
		                                		</a>		                                		
		                                		<div class="popover popover-tooltip top show-hide-details-popover" ng-show="showElement[group.groupId+'-showHideDetails'] == true"> 
											     	<div class="arrow"></div>
											        <div class="popover-content">											        	
											        	<span ng-show="moreInfo[group.groupId] == false || moreInfo[group.groupId] == null">Show Details</span>									                    
											        	<span ng-show="moreInfo[group.groupId] == true">Hide Details</span>
											        </div>                
											    </div>
											</li>	
                                            <#if !(isPublicProfile??)>                                    	
                                            <li>
                                                <@orcid.privacyToggle2 angularModel="group.getActive().visibility"
                                                    questionClick="toggleClickPrivacyHelp(group.getActive().putCode)"
                                                    clickedClassCheck="{'popover-help-container-show':privacyHelp[group.getActive().putCode.value]==true}"
                                                    publicClick="worksSrvc.setGroupPrivacy(group.getActive().putCode.value, 'PUBLIC', $event)"
                                                    limitedClick="worksSrvc.setGroupPrivacy(group.getActive().putCode.value, 'LIMITED', $event)"
                                                    privateClick="worksSrvc.setGroupPrivacy(group.getActive().putCode.value, 'PRIVATE', $event)"/>
                                            </li>
                                            </#if>
                                        </ul>
                                    </div>                                	
                            </div>
                        </div>
                        
                        
                    </div>
                </li> 
                <!-- End of Header -->
                
                              
                <li ng-repeat="work in group.activities" ng-show="group.activePutCode == work.putCode.value || editSources[group.groupId] == true" orcid-put-code="{{work.putCode.value}}">
                    <!-- active row summary info -->
                    <div class="row" ng-show="group.activePutCode == work.putCode.value">
                        <div class="col-md-9 col-sm-12 col-xs-12">
                            <h3 class="workspace-title">
                                <span ng-bind="work.title.value"></span>
                                <span class="journaltitle" ng-show="work.journalTitle.value" ng-bind="':&nbsp;'.concat(work.journalTitle.value)"></span>                                     
                            </h3>
                            <div ng-show="bulkEditShow == true" class="bulk-edit-input hidden-lg hidden-md hidden-sm pull-right">
				        		<input type="checkbox" ng-model="bulkEditMap[work.putCode.value]" class="ng-pristine ng-valid">
				        	</div>
                            <div class="info-detail">
                                <span ng-show="work.publicationDate.year">{{work.publicationDate.year}}</span><span ng-show="work.publicationDate.month">-{{work.publicationDate.month}}</span><span ng-show="work.publicationDate.year"> | </span> <span class="uppercase">{{work.workType.value}}</span>                  
                            </div>                            
                        </div>
                        
                        
                            <div class="col-md-3 workspace-toolbar">
                                <ul class="workspace-private-toolbar" ng-hide="editSources[group.groupId] == true">
                                	<#if !(isPublicProfile??)>
	                                	<!-- Bulk edit tool -->
		                                <li ng-show="bulkEditShow == true" class="hidden-xs bulk-checkbox-item">								
							        			<input type="checkbox" ng-model="bulkEditMap[work.putCode.value]" class="bulk-edit-input ng-pristine ng-valid pull-right">			        										
										</li>
									</#if>	
                                	<!-- Show/Hide Details -->
                                	<li class="works-details" ng-hide="editSources[group.groupId] == true">                                		
                                		<a ng-click="showDetailsMouseClick(group,$event);" ng-mouseenter="showTooltip(group.groupId+'-showHideDetails')" ng-mouseleave="hideTooltip(group.groupId+'-showHideDetails')">
                                			<span ng-class="(moreInfo[group.groupId] == true) ? 'glyphicons book' : 'glyphicons book_open'">
                                			</span>
                                		</a>                                		
                                		<div class="popover popover-tooltip top show-hide-details-popover" ng-show="showElement[group.groupId+'-showHideDetails'] == true"> 
									     	<div class="arrow"></div>
									        <div class="popover-content">
									        	<span ng-show="moreInfo[group.groupId] == false || moreInfo[group.groupId] == null">Show Details</span>									                    
									        	<span ng-show="moreInfo[group.groupId] == true">Hide Details</span>
									        </div>                
									    </div>                                		
									</li>

									<#if !(isPublicProfile??)>
	                                    <!-- Privacy -->
	                                    <li>
	                                        <@orcid.privacyToggle2 angularModel="work.visibility"
	                                            questionClick="toggleClickPrivacyHelp(group.highestVis())"
	                                            clickedClassCheck="{'popover-help-container-show':privacyHelp[work.putCode.value]==true}"
	                                            publicClick="worksSrvc.setGroupPrivacy(work.putCode.value, 'PUBLIC', $event)"
	                                            limitedClick="worksSrvc.setGroupPrivacy(work.putCode.value, 'LIMITED', $event)"
	                                            privateClick="worksSrvc.setGroupPrivacy(work.putCode.value, 'PRIVATE', $event)"/>
	                                    </li>
	                                </#if>
                                </ul>
                                <#if !(isPublicProfile??)>
                                    <div ng-show="!group.consistentVis()">
                                        <i class="vis-issue glyphicons circle_exclamation_mark"></i>
                                    </div>
                                </#if>
                            </div>                        
                  	 </div>
                            
                     <!-- Active Row Identifiers / URL / Validations / Versions -->
                     <div class="row" ng-show="group.activePutCode == work.putCode.value">
                         <div class="col-md-12 col-sm-12 bottomBuffer">
                             <ul class="id-details">         
                                 <li>
                                     <span ng-repeat='ie in work.workExternalIdentifiers'><span
                                     ng-bind-html='ie | workExternalIdentifierHtml:$first:$last:work.workExternalIdentifiers.length'></span>
                                    </span>
                                 </li>
                                 <li ng-show="work.url.value">URL: <a href="{{work.url.value | urlWithHttp}}" target="_blank">{{work.url.value}}</a></li>
                                 
                             </ul>
                         </div>
                     </div> 
                       
                     
                     <!-- more info -->
                     <#include "work_more_info_inc_v3.ftl"/>
                         
                     <!-- active row  source display -->
                      <div class="row source-line" ng-show="group.activePutCode == work.putCode.value">                      
                      	<div class="col-md-4" ng-show="editSources[group.groupId] == true">
                      		{{work.sourceName}}
                      	</div>
                      	<div class="col-md-3" ng-show="editSources[group.groupId] == true">
                      		<div ng-show="editSources[group.groupId] == true" ng-bind="work.createdDate | ajaxFormDateToISO8601"></div>
                      	</div>              
                      	<div class="col-md-3" ng-show="editSources[group.groupId] == true">
                     		
							<div ng-show="editSources[group.groupId] == true">
						        <span class="glyphicon glyphicon-check ng-hide" ng-show="work.putCode.value == group.defaultPutCode"></span><span ng-show="work.putCode.value == group.defaultPutCode"> Preferred source</span>
						        <#if !(isPublicProfile??)>              
							        <a ng-click="worksSrvc.makeDefault(group, work.putCode.value); " ng-show="work.putCode.value != group.defaultPutCode" class="">
							         	<span class="glyphicon glyphicon-unchecked"></span> Make preferred
							        </a>
						        </#if>
							</div>    
						
                      	</div>
                      	<div class="col-md-2 trash-source" ng-show="editSources[group.groupId] == true">
                      		<div ng-show="editSources[group.groupId] == true">
					        <#if !(isPublicProfile??)>
					        	<ul class="sources-actions">
					        		<#if RequestParameters['combine']??>
						        		<li ng-show="canBeCombined(work)">
						        			<a class="glyphicons git_pull_request" ng-click="showCombineMatches(group.getDefault())" ng-mouseenter="showTooltip(work.putCode.value+'-combineActiveDuplicates')" ng-mouseleave="hideTooltip(work.putCode.value+'-combineActiveDuplicates')"></a>
						        			
						        			<div class="popover popover-tooltip top combine-activeDuplicates-popover" ng-show="showElement[work.putCode.value+'-combineActiveDuplicates'] == true"> 
											    <div class="arrow"></div>
											    <div class="popover-content">
											        Combine duplicates                                      
											    </div>                
											</div>
						        			
						        			
						        		</li>
						        	</#if>					        		
					        		<li>
					        			<a ng-show="!group.hasUserVersion() || userIsSource(work)" ng-click="openEditWork(group.getActive().putCode.value)" ng-mouseenter="showTooltip(group.groupId+'-editActiveSource')" ng-mouseleave="hideTooltip(group.groupId+'-editActiveSource')">
											<span class="glyphicon glyphicon-pencil" ng-class="{'glyphicons git_create' : !userIsSource(work)}"></span>
										</a>
										
										<div class="popover popover-tooltip top edit-activeSource-popover" ng-show="showElement[group.groupId+'-editActiveSource'] == true"> 
										    <div class="arrow"></div>
										    <div class="popover-content">
										        <span ng-hide="!userIsSource(work)">Edit my version</span>
												<span ng-show="!userIsSource(work)">Make a copy and edit</span>
										    </div>                
										</div>
					        		</li>
					        		<li>
					        			<a ng-click="deleteWorkConfirm(work.putCode.value, false)"  title="Delete {{work.title.value}}" ng-mouseenter="showTooltip(work.putCode.value+'-deleteActiveSource')" ng-mouseleave="hideTooltip(work.putCode.value+'-deleteActiveSource')">
											<span class="glyphicon glyphicon-trash"></span>
										</a>
										
										<div class="popover popover-tooltip top delete-activeSource-popover" ng-show="showElement[work.putCode.value+'-deleteActiveSource'] == true"> 
										    <div class="arrow"></div>
										    <div class="popover-content">
										        Delete this source                                      
										    </div>                
										</div>
										
										
										
					        		</li>
					        	</ul>
							</#if>
						</div>
                      	</div>
                    </div>  
                    
                    
                    <!-- not active row && edit sources -->
                    <div ng-show="group.activePutCode != work.putCode.value" class="row source-line">                       
                        <div class="col-md-4 col-sm-4 col-xs-4">
                                <a ng-click="group.activePutCode = work.putCode.value;">
                                {{work.sourceName}}
                            </a> 
                        </div>
                        <div class="col-md-3 col-sm-3 col-xs-3" ng-bind="work.createdDate | ajaxFormDateToISO8601"></div>
                        <div class="col-md-3 col-sm-3 col-xs-5">
                        	 <#if !(isPublicProfile??)>                        	 	
	                            <span class="glyphicon glyphicon-check" ng-show="work.putCode.value == group.defaultPutCode"></span><span ng-show="work.putCode.value == group.defaultPutCode"> Preferred source</span>	                             
	                            <a ng-click="worksSrvc.makeDefault(group, work.putCode.value); " ng-show="work.putCode.value != group.defaultPutCode">
	                               <span class="glyphicon glyphicon-unchecked"></span> Make preferred
	                            </a>
                            </#if> 
                        </div>
                        <div class="col-md-2 col-sm-2 col-xs-12 trash-source">
                            <#if !(isPublicProfile??)>
                                <ul class="sources-actions">
                                	<#if RequestParameters['combine']??>
	                                	<li ng-show="canBeCombined(work)">					        			
						        			<a class="glyphicons git_pull_request" ng-click="showCombineMatches(group.getDefault())" ng-mouseenter="showTooltip(work.putCode.value+'-combineInactiveDuplicates')" ng-mouseleave="hideTooltip(work.putCode.value+'-combineInactiveDuplicates')"></a>					        			
						        			
						        			<div class="popover popover-tooltip top combine-inactiveDuplicates-popover" ng-show="showElement[work.putCode.value+'-combineInactiveDuplicates'] == true"> 
											    <div class="arrow"></div>
											    <div class="popover-content">
											        Combine duplicates                                      
											    </div>                
											</div>
						        			
						        		</li>
					        		</#if>
                                    <li>
                                        <a ng-show="!group.hasUserVersion() || userIsSource(work)" ng-click="openEditWork(work.putCode.value)" ng-mouseenter="showTooltip(work.putCode.value+'-editInactiveSource')" ng-mouseleave="hideTooltip(work.putCode.value+'-editInactiveSource')">
                                            <span class="glyphicon glyphicon-pencil" ng-class="{'glyphicons git_create' : !userIsSource(work)}"></span>
                                        </a>
                                        <div class="popover popover-tooltip top edit-inactiveSource-popover" ng-show="showElement[work.putCode.value+'-editInactiveSource'] == true"> 
										    <div class="arrow"></div>
										    <div class="popover-content">
										        <span ng-hide="!userIsSource(work)">Edit my version</span>
												<span ng-show="!userIsSource(work)">Make a copy and edit</span>
										    </div>                
										</div>
                                    </li>
                                    <li>
                                        <a ng-click="deleteWorkConfirm(work.putCode.value, false)" ng-mouseenter="showTooltip(work.putCode.value+'-deleteInactiveSource')" ng-mouseleave="hideTooltip(work.putCode.value+'-deleteInactiveSource')">
                                            <span class="glyphicon glyphicon-trash" title="Delete {{work.title.value}}"></span>
                                        </a>
                                        
                                        <div class="popover popover-tooltip top delete-inactiveSource-popover" ng-show="showElement[work.putCode.value+'-deleteInactiveSource'] == true"> 
										    <div class="arrow"></div>
										    <div class="popover-content">
										        Delete this source                                      
										    </div>                
										</div>
                                        
                                        
                                    </li>
                                </ul>
                            </#if>
                        </div>                      
                    </div>
                    
                    
                    <!--  Final Row -->                     
                    <div class="row source-line" ng-hide="editSources[group.groupId] == true">
                    	<div class="col-md-4">
                      		SOURCE: {{work.sourceName}}
                      	</div>
                      	<div class="col-md-3">                      		
                      		CREATED: <span ng-bind="work.createdDate | ajaxFormDateToISO8601"></span>                      		
                      	</div>
                      	<div class="col-md-3">                     								
							    <span class="glyphicon glyphicon-check"></span><span> Preferred source</span> <span ng-hide="group.activitiesCount == 1">(</span><a ng-click="editSources[group.groupId] = !editSources[group.groupId]" ng-hide="group.activitiesCount == 1" ng-mouseenter="showTooltip(group.groupId+'-sources')" ng-mouseleave="hideTooltip(group.groupId+'-sources')">of {{group.activitiesCount}}</a><span ng-hide="group.activitiesCount == 1">)</span>
							    
							    <div class="popover popover-tooltip top sources-popover" ng-show="showElement[group.groupId+'-sources'] == true"> 
								    <div class="arrow"></div>
								    <div class="popover-content">
								    	Show other sources								        
								    </div>                
								</div>
                      	</div>                    

                    	<div class="col-md-2" ng-show="group.activePutCode == work.putCode.value">                    	
                    		<ul class="sources-options" ng-cloak>                    			
							    <#if !(isPublicProfile??)>
							    	<#if RequestParameters['combine']??>
                                        <li ng-show="canBeCombined(work)">
                                            <a ng-click="showCombineMatches(group.getDefault())" title="Combine duplicates" ng-mouseenter="showTooltip(group.groupId+'-combineDuplicates')" ng-mouseleave="hideTooltip(group.groupId+'-combineDuplicates')">
                                                <span class="glyphicons git_pull_request"></span>
                                            </a>
                                            
                                            <div class="popover popover-tooltip top combine-duplicates-popover" ng-show="showElement[group.groupId+'-combineDuplicates'] == true"> 
                                                <div class="arrow"></div>
                                                <div class="popover-content">
                                                    Combine duplicates
                                                </div>                
                                            </div>
                                            
                                            
                                        </li>
                                    </#if>							    
							    
							    
							    	<li ng-show="userIsSource(work) || (group.hasKeys() && !group.hasUserVersion())">
									    <a ng-click="openEditWork(group.getActive().putCode.value)" ng-mouseenter="showTooltip(group.groupId+'-editSource')" ng-mouseleave="hideTooltip(group.groupId+'-editSource')">
									        <span class="glyphicon glyphicon-pencil" ng-class="{'glyphicons git_create' : !userIsSource(work)}" title=""></span>
									    </a>
									    <div class="popover popover-tooltip top edit-source-popover" ng-show="showElement[group.groupId+'-editSource'] == true"> 
										    <div class="arrow"></div>
										    <div class="popover-content">
										        <span ng-hide="!userIsSource(work)">Edit my version</span>
										        <span ng-show="!userIsSource(work)">Make a copy and edit</span>
										    </div>                
										</div>
									</li>
							         
							         <li ng-hide="editSources[group.groupId] == true || group.activitiesCount == 1">
							            <a ng-click="editSources[group.groupId] = !editSources[group.groupId]" ng-mouseenter="showTooltip(group.groupId+'-deleteGroup')" ng-mouseleave="hideTooltip(group.groupId+'-deleteGroup')">
											<span class="glyphicon glyphicon-trash"></span>
										</a>
										
										<div class="popover popover-tooltip top delete-group-popover" ng-show="showElement[group.groupId+'-deleteGroup'] == true"> 
										 	<div class="arrow"></div>
										    <div class="popover-content">
										    	Delete this source	        
										    </div>                
										</div>
										
							         </li>
							         
							         <li ng-show="group.activitiesCount == 1">
										<a ng-click="deleteWorkConfirm(group.getActive().putCode.value, false)" ng-mouseenter="showTooltip(group.groupId+'-deleteSource')" ng-mouseleave="hideTooltip(group.groupId+'-deleteSource')">
										   <span class="glyphicon glyphicon-trash"></span>
										</a>
										
										
										<div class="popover popover-tooltip top delete-source-popover" ng-show="showElement[group.groupId+'-deleteSource'] == true"> 
										 	<div class="arrow"></div>
										    <div class="popover-content">
										    	Delete this source								        
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
<div ng-show="worksSrvc.loading == true" class="text-center" id="workSpinner">
    <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i><!-- Hidden with a CSS hack on IE 7 only -->
    <!--[if lt IE 8]>    
        <img src="${staticCdn}/img/spin-big.gif" width="85" height ="85"/>
    <![endif]-->
</div>
<div ng-show="worksSrvc.loading == false && worksSrvc.groups.length == 0" class="" ng-cloak>
    <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_works_body_list.Nopublicationsaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_works_body_list.havenotaddedanyworks")} <a ng-click="showWorkImportWizard()">${springMacroRequestContext.getMessage("workspace_works_body_list.addsomenow")}</a></#if></strong>
</div>