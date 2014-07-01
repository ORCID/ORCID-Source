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
		            	<li class="submenu-tree">
		            		<a href="" class="toolbar-button toggle-menu">
		            			<span class="glyphicon glyphicon-align-left edit-option-toolbar"></span>
		            		</a>
		            		<ul class="workspace-submenu-options">
		            			<li>
		            				<a href="">
		            					<span class="glyphicon glyphicon-file"></span>Review Versions
		            				</a>
		            			</li>
		            			<li>
		            				<a ng-click="worksSrvc.makeDefault(group, group.getActive().putCode.value)">
		            					<span class="glyphicon glyphicon-file"></span>Make Default
		            				</a>
		            			</li>
		            			<li>
		            				<a ng-click="deleteWorkConfirm(group.getActive().putCode.value, true)">
		            					<span class="glyphicon glyphicon-trash"></span>Delete All Versions
		            				</a>
		            			</li>
		            			<li>
		            				<a ng-click="deleteWorkConfirm(group.getActive().putCode.value, false)">
		            					<span class="glyphicon glyphicon-trash"></span>Delete This Version
		            				</a>
		            			</li>
		            			<li>
		            				<a href="">
		            					<span class="glyphicon glyphicon-question-sign"></span>Help
		            					</a>
		            			</li>
		            		</ul>
		            	</li>	
					</ul>				
				</#if>				
			</div>
        </div>
        
        <!-- Identifiers / URL / Validations / Versions -->
		<div class="row bottomBuffer">
			<div class="col-md-9 col-sm-9">
				<ul class="id-details">				
					<li>
						<span ng-repeat='ie in group.getActive().workExternalIdentifiers'><span
						ng-bind-html='ie | workExternalIdentifierHtml:$first:$last:group.getActive().workExternalIdentifiers.length'></span>
					   </span>
					</li>
					<li ng-show="group.getActive().url.value"><strong>URL:</strong> <a href="{{group.getActive().url.value | urlWithHttp}}" target="_blank">{{group.getActive().url.value}}</a></li>
				</ul>
			</div>
			<div class="col-md-3 col-sm-3">
				<ul class="validations-versions nav nav-pills nav-stacked">
					<li><a href=""><span class="glyphicon glyphicon-ok green"></span><strong></strong><span class="badge pull-right blue">0</span>Validated</a></li>
					<li><a href=""><span class="glyphicon glyphicon-file green"></span><span class="badge pull-right blue" ng-bind="group.activitiesCount"></span>Versions</a></li> <!-- for non versions use class 'opaque' instead green -->
				</ul>
			</div>
		</div>        
       	
       	<!-- More info -->
       	<#include "work_more_info_inc_v2.ftl"/>      
        
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
    