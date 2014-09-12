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
<div class="row sources-details">
	<div class="col-md-12 col-sm-12">
		<!-- Sources -->			
		<div class="sources-container-header">			
			<div class="row">					 
				
				<div class="col-md-5" ng-hide="editSources[group.groupId] == true">
					<span>
						<strong >Source:</strong> {{group.getActive().sourceName.value}}
					</span>						
				</div>
				
				<div ng-class="editSources[group.groupId] == true ? 'col-md-12' : 'col-md-7'">						
					<ul class="sources-options" ng-cloak>
						<li ng-hide="group.activitiesCount == 1 || editSources[group.groupId] == true">							
							<span class="view-sources-details">
							 	<a ng-click="editSources[group.groupId] = !editSources[group.groupId]">View <span class="badge">{{group.activitiesCount - 1 }}</span> additional source<span ng-show="group.activitiesCount > 2">s</span></a>							 	
							</span>
							<a ng-click="editSources[group.groupId] = !editSources[group.groupId]">
			            	   <span class="glyphicon glyphicon-trash"></span>
			        		</a>
						</li>
						<li ng-show="group.activitiesCount == 1">
							<a ng-click="deleteFundingConfirm(group.getActive().putCode.value, false)">
			            	   <span class="glyphicon glyphicon-trash"></span>
			               </a>
						</li>
				        <li ng-show="editSources[group.groupId] == true">
				            <a ng-click="deleteFundingConfirm(group.getActive().putCode.value, true)">
				                <span class="glyphicon glyphicon-trash"></span> Delete all
				            </a>
				        </li>
				        <li ng-show="editSources[group.groupId] == true">
				            <a ng-click="editSources[group.groupId] = false">
				                <span class="glyphicon glyphicon-remove"></span> Hide additional sources
				            </a>
				        </li>
				        <li>
					        <div class="show-more-info-tab work-tab">			
								<a href="" ng-show="!moreInfo[group.getActive().putCode.value]" ng-click="showDetailsMouseClick(group.getActive().putCode.value,$event);"><span class="glyphicon glyphicon-chevron-down"></span><@orcid.msg 'manage.developer_tools.show_details'/></a>									
								<a href="" ng-show="moreInfo[group.getActive().putCode.value]" ng-click="showDetailsMouseClick(group.getActive().putCode.value,$event);"><span class="glyphicon glyphicon-chevron-up"></span><@orcid.msg 'manage.developer_tools.hide_details'/></a>
							</div>							
				        </li>                               
				    </ul>
				</div>
			</div>
		</div>
	</div>
	<div class="col-md-12 col-sm-12">
		<div class="sources-container">
			<div class="sources-edit">	
				<ul class="sources-edit-list" ng-show="editSources[group.groupId] == true" ng-cloak>
					<li class="first-source">
						<div class="col-sm-4">
							<span>
					        	<strong >Source:</strong> {{group.getActive().sourceName}}
					        </span>
				        </div>
				        <div class="col-sm-4">
				        	Last modified: {{group.getActive().lastModified | ajaxFormDateToISO8601}}
				        </div>
				        <div class="col-sm-3">
				        	   <span class="glyphicon glyphicon-check" ng-show="group.getActive().putCode.value == group.defaultPutCode"></span> 
					           <a ng-click="fundingSrvc.makeDefault(group, group.getActive().putCode.value); group.activePutCode = group.getActive().putCode.value" ng-show="group.getActive().putCode.value != group.defaultPutCode">
				            	 <span class="glyphicon glyphicon-unchecked"></span> Make Preferred
				               </a>
				        </div>
				        <div class="col-sm-1">
				        		<a ng-click="deleteWorkConfirm(group.getActive().putCode.value, false)">
			            	   <span class="glyphicon glyphicon-trash"></span>
			               </a>
				        </div>
					</li>
					<li ng-repeat="funding in group.activities" ng-hide="group.activePutCode == funding.putCode.value">
						<div class="col-sm-4">
							<a ng-click="worksSrvc.showSpinner($event); moreInfo[funding.putCode.value] = moreInfo[group.activePutCode]; group.activePutCode = funding.putCode.value">
				           		{{funding.sourceName}}
				           	</a> 
						</div>
						<div class="col-sm-4">
							{{funding.lastModified | ajaxFormDateToISO8601}}
						</div>
						<div class="col-sm-3">
							<span class="glyphicon glyphicon-check" ng-show="funding.putCode.value == group.defaultPutCode"></span> 
				           <a ng-click="fundingSrvc.makeDefault(group, funding.putCode.value); " ng-show="funding.putCode.value != group.defaultPutCode">
			            	 <span class="glyphicon glyphicon-unchecked"></span> Make Preferred
			               </a>
						</div>
						<div class="col-sm-1">
							<a ng-click="deleteWorkConfirm(group.getActive().putCode.value, false)">
			            	   <span class="glyphicon glyphicon-trash"></span>
			               </a>
						</div>
					</li>
				</ul>				
			</div>
		</div>
	</div>
</div>