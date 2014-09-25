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
<div class="row">        
  	<!-- Information -->
	<div class="col-md-9 col-sm-9">
	    <h3 class="workspace-title">
        	<strong ng-bind-html="group.getActive().affiliationName.value"></strong>:
        	<span ng-bind="group.getActive().city.value"></span><span ng-show="group.getActive().region.value">, </span><span ng-bind="group.getActive().region.value"></span>, <span ng-bind="group.getActive().country.value"></span>        	        	        	        	
        </h3>        
        <div class="info-detail">
        	<div class="info-date">
        	        	
	        	<span class="affiliation-date" ng-show="group.getActive().startDate && !group.getActive().endDate">
	        	    <span ng-show="group.getActive().startDate.year">{{group.getActive().startDate.year}}</span><span ng-show="group.getActive().startDate.month">-{{group.getActive().startDate.month}}</span>
	        	    <span><@orcid.msg 'workspace_affiliations.dateSeparator'/></span>
	        	     <@orcid.msg 'workspace_affiliations.present'/>
	        	</span>
	        	
	        	<span class="affiliation-date" ng-show="group.getActive().startDate && group.getActive().endDate">
	        		<span ng-show="group.getActive().startDate.year">{{group.getActive().startDate.year}}</span><span ng-show="group.getActive().startDate.month">-{{group.getActive().startDate.month}}</span>
	        		<@orcid.msg 'workspace_affiliations.dateSeparator'/>
	        		<span ng-show="group.getActive().endDate.year">{{group.getActive().endDate.year}}</span><span ng-show="group.getActive().endDate.month">-{{group.getActive().endDate.month}}</span>
	            </span>
	            
	            <span class="affiliation-date" ng-show="!group.getActive().startDate && group.getActive().endDate">
	        	     <span ng-show="group.getActive().endDate.year">{{group.getActive().endDate.year}}</span><span ng-show="group.getActive().endDate.month">-{{group.getActive().endDate.month}}</span>
	        	</span>
	        	        	
        	</div>
        	<span class="divisor" ng-show="group.getActive().roleTitle && (group.getActive().startDate || group.getActive().endDate)"></span>
        	
        	<div class="role" ng-show="group.getActive().roleTitle">
	            <span ng-bind-html="group.getActive().roleTitle.value"></span>
        	</div>
        </div>        
       </div>
       <!-- Privacy Settings -->
       <div class="col-md-3 col-sm-3 workspace-toolbar">       	
       	<#if !(isPublicProfile??)>
       		<a href ng-click="deleteAffiliation(group.getActive())" class="glyphicon glyphicon-trash grey"></a>
       		<ul class="workspace-private-toolbar">
       			<li>
			 		<a href="" class="toolbar-button edit-item-button">
						<span class="glyphicon glyphicon-pencil edit-option-toolbar" title="" ng-click="openEditAffiliation(group.getActive())"></span>
					</a>	
			 	</li>	
			 	<li>
					<@orcid.privacyToggle2  angularModel="group.getActive().visibility.visibility"
					questionClick="toggleClickPrivacyHelp(group.getActive().putCode.value)"
					clickedClassCheck="{'popover-help-container-show':privacyHelp[group.getActive().putCode.value]==true}" 
					publicClick="setPrivacy(group.getActive(), 'PUBLIC', $event)" 
	                  	limitedClick="setPrivacy(group.getActive(), 'LIMITED', $event)" 
	                  	privateClick="setPrivacy(group.getActive(), 'PRIVATE', $event)" />
                </li>			        
        	</ul>
        </#if>
	</div>
</div>
<div class="row">
	<div class="col-md-12">		
	
	</div>
</div>
<div class="content affiliate" ng-show="moreInfo[group.getActive().putCode.value]">	
	<div class="row">			
		<div class="col-md-12">
			<#include "affiliate_more_info_inc_v3.ftl"/>
		</div>
	</div>
</div>	
<div class="row">
	<div class="col-md-12 col-sm-12 col-xs-12">
		<div class="row">
			<div class="col-md-5">
				<strong>Source:</strong> {{group.getActive().sourceName}}	
			</div>
			<div class="col-md-7">
				<div class="show-more-info-tab">			
					<a href="" ng-show="!moreInfo[group.getActive().putCode.value]" ng-click="showDetailsMouseClick(group.getActive().putCode.value,$event);"><span class="glyphicon glyphicon-chevron-down"></span><@orcid.msg 'manage.developer_tools.show_details'/></a>
					<a href="" ng-show="moreInfo[group.getActive().putCode.value]" ng-click="showDetailsMouseClick(group.getActive().putCode.value,$event);"><span class="glyphicon glyphicon-chevron-up"></span><@orcid.msg 'manage.developer_tools.hide_details'/></a>
				</div>
			</div>
		</div>
	</div>
</div>
