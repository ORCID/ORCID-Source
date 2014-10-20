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
			<span ng-show="group.getActive().departmentName.value">&nbsp;(</span><span ng-show="group.getActive().departmentName.value" ng-bind="group.getActive().departmentName.value" ng-cloak></span><span ng-show="group.getActive().departmentName.value">)</span>
        </div>        
       </div>
       <!-- Privacy Settings -->
       <div class="col-md-3 col-sm-3 workspace-toolbar">       	
       	<#if !(isPublicProfile??)>       		
       		<ul class="workspace-private-toolbar">
       			<li ng-show="group.getActive().sourceOrcid == '${effectiveUserOrcid}'">
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
	<div class="col-md-12 col-sm-12 col-xs-12">
		<div class="sources-container-header">          
			<div class="row">
				<div class="col-md-5">
					<strong>Source:</strong> {{group.getActive().sourceName}}	
				</div>
				<div class="col-md-7">
					<ul class="sources-options">
						<#if !(isPublicProfile??)>   
					        <li>
					            <a href ng-click="deleteAffiliation(group.getActive())" class="glyphicon glyphicon-trash grey"></a>
					        </li>
				         </#if>  
					</ul>
				</div>
			</div>
		</div>
	</div>
</div>
