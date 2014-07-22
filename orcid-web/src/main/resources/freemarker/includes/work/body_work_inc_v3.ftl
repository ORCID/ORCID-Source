<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2013 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<ul ng-hide="!worksSrvc.groups.length" class="workspace-publications workspace-body-list bottom-margin-medium" id="body-work-list" ng-cloak>
    <li class="bottom-margin-small" ng-repeat="group in worksSrvc.groups | orderBy:['-dateSortString', 'title']">        
		<div class="row"> 
			<!-- Main title -->
			<div class="col-md-9 col-sm-9 col-xs-12">
		        <h3 class="workspace-title">
		        	<strong ng-bind="group.getActive().workTitle.title.value"></strong><span class="work-subtitle" ng-show="group.getActive().workTitle.subtitle.value" ng-bind="':&nbsp;'.concat(group.getActive().workTitle.subtitle.value)"></span>		        			        	
		        </h3>
		        <div class="info-detail">
		        	<span ng-show="group.getActive().publicationDate.year">{{group.getActive().publicationDate.year}}</span><span ng-show="group.getActive().publicationDate.month">-{{group.getActive().publicationDate.month}}</span>
		        </div>			        	        
	        </div>
	        <!-- Settings -->
	        <div class="col-md-3 col-sm-3 col-xs-12 workspace-toolbar">	        	
	        	<#if !(isPublicProfile??)>
	        		<!-- Privacy bar -->
					<ul class="workspace-private-toolbar">
					 	<li>
					 		<a class="toolbar-button edit-item-button" ng-click="openEditWork(group.getActive().putCode.value)">
					 			<span class="glyphicon glyphicon-pencil edit-option-toolbar" title=""></span>
					 		</a>	
					 	</li>					 	
						<li>
						<@orcid.privacyToggle2 angularModel="group.getActive().visibility" 
						    questionClick="toggleClickPrivacyHelp(group.getActive().putCode.value)"
						    clickedClassCheck="{'popover-help-container-show':privacyHelp[group.getActive().putCode.value]==true}"
							publicClick="worksSrvc.setGroupPrivacy(group.getActive().putCode.value, 'PUBLIC', $event)" 
		                	limitedClick="worksSrvc.setGroupPrivacy(group.getActive().putCode.value, 'LIMITED', $event)" 
		                	privateClick="worksSrvc.setGroupPrivacy(group.getActive().putCode.value, 'PRIVATE', $event)"/>
		                </li>
		            					</#if>				
			</div>
        </div>
        
        <!-- Identifiers / URL / Validations / Versions -->
		<div class="row bottomBuffer">
			<div class="col-md-12 col-sm-12">
				<ul class="id-details">				
					<li>
						<span ng-repeat='ie in group.getActive().workExternalIdentifiers'><span
						ng-bind-html='ie | workExternalIdentifierHtml:$first:$last:group.getActive().workExternalIdentifiers.length'></span>
					   </span>
					</li>
					<li ng-show="group.getActive().url.value"><strong>URL:</strong> <a href="{{group.getActive().url.value | urlWithHttp}}" target="_blank">{{group.getActive().url.value}}</a></li>
				</ul>
			</div>
		</div>        
       	
       	
        <!-- Identifiers / URL / Validations / Versions -->
		<div class="row bottomBuffer">
			<div class="col-md-12 col-sm-12">
				<!-- Sources -->			
				<div class="sources-container">
					<div>
						<div class="row">
							<div class="col-md-8">
								<strong>Source:</strong> {{group.getActive().workSourceName.value}}
								<span ng-hide="group.activitiesCount == 1" class="pull-right">
								 (<a ng-click="editSources[group.groupId] = !editSources[group.groupId]">{{group.activitiesCount - 1 }} additional source<span ng-show="group.activitiesCount > 2">s</span></a>)
								</span>
							</div>
							<div class="col-md-4">
								<ul class="sources-options" ng-show="editSources[group.groupId] == true" ng-cloak>
									<li>
										<a ng-click="deleteWorkConfirm(group.getActive().putCode.value, true)">
		                 					<span class="glyphicon glyphicon-trash"></span> Delete all
		              					</a>
									</li>
									<li>
										<a ng-click="editSources[group.groupId] = false">
											<span class="glyphicon glyphicon-remove"></span> Close
										</a>
									</li>								
								</ul>
								
							</div>
						</div>
						<div class="sources-edit">	
							<table class="sources-edit-table" ng-show="editSources[group.groupId] == true" ng-cloak>							    
							    <tr ng-repeat="work in group.activities" ng-class="work.putCode.value == group.activePutCode ? 'grey-box' : ''">
							       <td>
							       		<span
							       		    ng-show="work.putCode.value == group.activePutCode"
							           		ng-click="moreInfo[work.putCode.value] = moreInfo[group.activePutCode]; group.activePutCode = work.putCode.value">
							           			{{work.workSourceName.value}}
							           		</span>
							           	<a ng-hide="work.putCode.value == group.activePutCode"
							           		ng-click="moreInfo[work.putCode.value] = moreInfo[group.activePutCode]; group.activePutCode = work.putCode.value">
							           			{{work.workSourceName.value}}
							           		</a> 
							       </td>
							       <td>
							           <span class="glyphicon glyphicon-globe privacy" ng-show="work.putCode.value == group.defaultPutCode"></span> 
							           <a ng-click="worksSrvc.makeDefault(group, work.putCode.value)" ng-show="work.putCode.value != group.defaultPutCode">
						            	 <span class="glyphicon glyphicon-file"></span> Make Default
						               </a>
						           </td>
							       <td>
							           <a ng-click="deleteWorkConfirm(group.getActive().putCode.value, false)">
						            	   <span class="glyphicon glyphicon-trash"></span> Delete
						               </a>
							       </td>
							    </tr>
							</table>						
						</div>						
					</div>
				</div>	
				 
				 <!-- 
				<strong>Sources:</strong>
				<a class="glyphicon glyphicon-pencil" ng-click="editSources[group.groupId] = true" ng-hide="editSources[group.groupId] == true"></a> 
				<span ng-repeat="work in group.activities" ng-hide="editSources[group.groupId] == true">					       
					  <span class="version-name">
						   <span class="glyphicon glyphicon-globe privacy" ng-show="work.putCode.value == group.defaultPutCode"></span>
						   <a ng-click="moreInfo[work.putCode.value] = moreInfo[group.activePutCode]; group.activePutCode = work.putCode.value" ng-bind="work.workSourceName.value" ng-class="work.putCode.value == group.activePutCode ? 'current-version' : ''"></a><span ng-show="$last == false">, </span>     
					  </span>
			    </span>
			    
			    
			    <table border="1" ng-show="editSources[group.groupId] == true" ng-cloak>
			    <tr>
			       <th>
			          Ugly edit table (<a ng-click="editSources[group.groupId] = false">close</a>)			           
			       </th>
			       <th>
			       </th>
			       <th>
			          <a ng-click="deleteWorkConfirm(group.getActive().putCode.value, true)">
		                 <span class="glyphicon glyphicon-trash"></span>Delete All
		              </a>
			       </th>
			    </tr>
			    <tr ng-repeat="work in group.activities">
			       <td style="padding: 15px;">
			           <a ng-click="moreInfo[work.putCode.value] = moreInfo[group.activePutCode]; group.activePutCode = work.putCode.value" ng-bind="work.workSourceName.value" ng-class="work.putCode.value == group.activePutCode ? 'current-version' : ''"></a>
			       </td>
			       <td style="padding: 15px;">
			           <span class="glyphicon glyphicon-globe privacy" ng-show="work.putCode.value == group.defaultPutCode"></span>
			           <a ng-click="worksSrvc.makeDefault(group, work.putCode.value)" ng-show="work.putCode.value != group.defaultPutCode">
		            	 <span class="glyphicon glyphicon-file"></span>Make Default
		               </a>
		           </td>
			       <td style="padding: 15px;">
			           <a ng-click="deleteWorkConfirm(group.getActive().putCode.value, false)">
		            	   <span class="glyphicon glyphicon-trash"></span>Delete
		               </a>
			       </td>
			    </tr>
			    </table>
			    -->
			     
			    
			</div>
		</div>        
       	
       	<!-- More info -->
       	<#include "work_more_info_inc_v3.ftl"/>      
        
        <!-- More info tabs -->
        <div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">
				<div class="show-more-info-tab">			
					<a href="" ng-show="!moreInfo[group.getActive().putCode.value]" ng-click="showDetailsMouseClick(group.getActive().putCode.value,$event);"><span class="glyphicon glyphicon-chevron-down"></span><@orcid.msg 'manage.developer_tools.show_details'/></a>
					<a href="" ng-show="moreInfo[group.getActive().putCode.value]" ng-click="showDetailsMouseClick(group.getActive().putCode.value,$event);"><span class="glyphicon glyphicon-chevron-up"></span><@orcid.msg 'manage.developer_tools.hide_details'/></a>
				</div>
			</div>
		</div>
    </li><!-- bottom-margin-small -->
</ul>
<div ng-show="worksSrvc.loading == true" class="text-center" id="workSpinner">
	<i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i><!-- Hidden with a CSS hack on IE 7 only -->
    <!--[if lt IE 8]>    
    	<img src="${staticCdn}/img/spin-big.gif" width="85" height ="85"/>
    <![endif]-->
</div>
<div ng-show="worksSrvc.loading == false && worksSrvc.works.length == 0" class="alert alert-info" ng-cloak>
    <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_works_body_list.Nopublicationsaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_works_body_list.havenotaddedanyworks")} <a ng-click="addWorkModal()">${springMacroRequestContext.getMessage("workspace_works_body_list.addsomenow")}</a></#if></strong>
</div>
    