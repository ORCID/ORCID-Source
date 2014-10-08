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
<ul ng-hide="!worksSrvc.groups.length" class="workspace-publications workspace-body-list bottom-margin-medium" id="body-work-list" ng-cloak>
    <li class="bottom-margin-small workspace-border-box card" ng-repeat="group in worksSrvc.groups | orderBy:sortPredicate:sortReverse">
    	<div class="row spinner">
    			<span class="glyphicon glyphicon-refresh spin x2 green">
    				
    			</span>
    	</div>
    	<div class="work-list-container">
			<div class="row"> 
				<!-- Main title -->
				<div class="col-md-9 col-sm-9 col-xs-12">
				    <h3 class="workspace-title">
			        	<strong ng-bind="group.getActive().workTitle.title.value"></strong><span class="work-subtitle" ng-show="group.getActive().workTitle.subtitle.value" ng-bind="':&nbsp;'.concat(group.getActive().workTitle.subtitle.value)"></span>		        			        	
			        </h3>
			        <div ng-show="showBulkEdit == true" class="bulk-edit-input hidden-lg hidden-md hidden-sm pull-right">
			        	<input type="checkbox" ng-model="bulkEditMap[group.getActive().putCode.value]"></input>
			        </div>
			        <div class="info-detail">
			        	<span ng-show="group.getActive().publicationDate.year">{{group.getActive().publicationDate.year}}</span><span ng-show="group.getActive().publicationDate.month">-{{group.getActive().publicationDate.month}}</span><span ng-show="group.getActive().publicationDate.year"> | </span> <span class="uppercase">{{group.getActive().workType.value}}</span>		        	
			        </div>		                	        
		        </div>
		        <!-- Settings -->
		        <div class="col-md-3 col-sm-3 col-xs-12 workspace-toolbar">	        	
		        	<#if !(isPublicProfile??)>
		        		<!-- Privacy bar -->
						<ul class="workspace-private-toolbar">
							<li ng-show="showBulkEdit" class="hidden-xs">								
			        			<input type="checkbox" ng-model="bulkEditMap[group.getActive().putCode.value]" class="bulk-edit-input"></input>			        										
							</li>							
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
			             </ul>
			        </#if>
			        <div ng-show="canBeCombined(group.getActive())">
			             <a ng-click="showCombineMatches(group.getDefault())">combined duplicates</a>
			        </div>				
				</div>
	        </div>
	        
	        <!-- Identifiers / URL / Validations / Versions -->
			<div class="row bottomBuffer" ng-show="group.getActive().workExternalIdentifiers.length > 0">
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
		</div>        
       	
       	<!-- More info -->
       	<#include "work_more_info_inc_v3.ftl"/>

       	<!-- Sources -->
       	<#include "work_sources_inc_v3.ftl"/>
       	        
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