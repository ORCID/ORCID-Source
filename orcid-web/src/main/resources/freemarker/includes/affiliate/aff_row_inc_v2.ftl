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
<div class="row">        
  	<!-- Information -->
	<div class="col-md-9 col-sm-9">
	    <h3 class="workspace-title">
        	<strong ng-bind-html="affiliation.affiliationName.value"></strong>:
        	<span ng-bind="affiliation.city.value"></span>, <span ng-bind="affiliation.region.value"></span>, <span ng-bind="affiliation.country.value"></span>        	        	        	        	
        </h3>        
        <div class="info-detail">
        	<div class="info-date">        	
	        	<span class="affiliation-date" ng-show="affiliation.startDate && !affiliation.endDate">
	        	    <span ng-show="affiliation.startDate.year">{{affiliation.startDate.year}}</span><span ng-show="affiliation.startDate.month">-{{affiliation.startDate.month}}</span>
	        	    <@orcid.msg 'workspace_affiliations.dateSeparator'/>
	        	    <@orcid.msg 'workspace_affiliations.present'/>
	        	</span>
	        	<span class="affiliation-date" ng-show="affiliation.startDate && affiliation.endDate">
	        		<span ng-show="affiliation.startDate.year">{{affiliation.startDate.year}}</span><span ng-show="affiliation.startDate.month">-{{affiliation.startDate.month}}</span>
	        		<@orcid.msg 'workspace_affiliations.dateSeparator'/>
	        		<span ng-show="affiliation.endDate.year">{{affiliation.endDate.year}}</span><span ng-show="affiliation.endDate.month">-{{affiliation.endDate.month}}</span>
	            </span>
	            <span class="affiliation-date" ng-show="!affiliation.startDate && affiliation.endDate">
	        	     <span ng-show="affiliation.endDate.year">{{affiliation.endDate.year}}</span><span ng-show="affiliation.endDate.month">-{{affiliation.endDate.month}}</span>
	        	</span>        	
        	</div>
        	<span class="divisor"></span>
        	<div class="role" ng-show="affiliation.roleTitle">
	            <span ng-bind-html="affiliation.roleTitle.value"></span>
        	</div>
        </div>        
       </div>
       <!-- Privacy Settings -->
       <div class="col-md-3 col-sm-3 workspace-toolbar">       	
       	<#if !(isPublicProfile??)>
       		<!-- <a href ng-click="deleteAffiliation(affiliation)" class="glyphicon glyphicon-trash grey"></a> -->
       		<ul class="workspace-private-toolbar">
       			<li>
			 		<a href="" class="toolbar-button edit-item-button">
			 			<span class="glyphicon glyphicon-pencil edit-option-toolbar" title=""></span>
			 		</a>	
			 	</li>	
			 	<li>
					<@orcid.privacyToggle2  angularModel="affiliation.visibility.visibility"
					questionClick="toggleClickPrivacyHelp(affiliation.putCode.value)"
					clickedClassCheck="{'popover-help-container-show':privacyHelp[affiliation.putCode.value]==true}" 
					publicClick="setPrivacy(affiliation, 'PUBLIC', $event)" 
	                  	limitedClick="setPrivacy(affiliation, 'LIMITED', $event)" 
	                  	privateClick="setPrivacy(affiliation, 'PRIVATE', $event)" />
                </li>			        
                <li class="submenu-tree">
            		<a href="" class="toolbar-button toggle-menu" id="more-options-button">
            			<span class="glyphicon glyphicon-align-left edit-option-toolbar"></span>
            		</a>
            		<ul class="workspace-submenu-options">
            			<li>
            				<a href=""><span class="glyphicon glyphicon-file"></span>Review Versions</a>
            			</li>
            			<li>
            				<a href=""><span class="glyphicon glyphicon-trash"></span>Delete</a>
            			</li>
            			<li>
            				<a href=""><span class="glyphicon glyphicon-question-sign"></span>Help</a>
            			</li>
            		</ul>
            	</li>
        	</ul>
        </#if>
	</div>
</div>
<div class="row">
	<div class="col-md-9">
		
	</div>
	<div class="col-md-3">
		<ul class="validations-versions nav nav-pills nav-stacked">
			<li><a href=""><span class="glyphicon glyphicon-ok green"></span><strong></strong><span class="badge pull-right blue">2</span>Validated</a></li>
			<li><a href=""><span class="glyphicon glyphicon-file green"></span><span class="badge pull-right blue">3</span>Versions</a></li> <!-- for non versions use class 'opaque' instead green -->
		</ul>
	</div>
</div>
<div class="content affiliate" ng-show="moreInfo[affiliation.putCode.value]">	
	<div class="row">			
		<div class="col-md-9">
			<#include "affiliate_more_info_inc_v2.ftl"/>
		</div>
		<div class="col-md-3">
			<!-- Validations -->					
			<div class="validations bottomBuffer">
				<strong>Validations</strong>
				<ul>
					<li><a href="">Validation #1</a></li>
					<li><a href="">Validation #2</a></li>
				</ul>
			</div>
			<!-- Versions -->
			<div class="versions bottomBuffer">
				<strong>Versions</strong>
				<ul>
					<li class="current-version"><span class="glyphicon glyphicon-chevron-right"></span><a href="">My Version</a><span class="glyphicon glyphicon-globe privacy"></span></li>
					<li><span class="glyphicon glyphicon-chevron-right"></span><a href="">ResearcherID</a></li>
					<li><span class="glyphicon glyphicon-chevron-right"></span><a href="">DataCite</a></li>
					<li><span class="glyphicon glyphicon-chevron-right"></span><a href="">Harvard University</a></li>
				</ul>
			</div>
			<!-- Work Source -->
			<div class="work-source bottombuffer" ng-show="worksSrvc.worksInfo[work.putCode.value].workSourceName.value" ng-cloak>
				<strong> <@orcid.msg
				'manual_work_form_contents.labelWorkSource'/></strong>
				<div ng-bind="worksSrvc.worksInfo[work.putCode.value].workSourceName.value"></div>
			</div>					
		</div>	
	</div>
</div>	
<div class="row">
	<div class="col-md-12 col-sm-12 col-xs-12">
		<div class="show-more-info-tab">			
			<a href="" ng-show="!moreInfo[affiliation.putCode.value]" ng-click="showDetailsMouseClick(affiliation.putCode.value,$event);"><span class="glyphicon glyphicon-chevron-down"></span><@orcid.msg 'manage.developer_tools.show_details'/></a>
			<a href="" ng-show="moreInfo[affiliation.putCode.value]" ng-click="showDetailsMouseClick(affiliation.putCode.value,$event);"><span class="glyphicon glyphicon-chevron-up"></span><@orcid.msg 'manage.developer_tools.hide_details'/></a>
		</div>
	</div>
</div>
