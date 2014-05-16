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
	    <h3 class="affiliation-title">
        	<strong ng-bind-html="affiliation.affiliationName.value"></strong>
        	<span class="affiliation-date" ng-show="affiliation.startDate && !affiliation.endDate">
        	    (<span ng-show="affiliation.startDate.year">{{affiliation.startDate.year}}</span><span ng-show="affiliation.startDate.month">-{{affiliation.startDate.month}}</span>
        	    <@orcid.msg 'workspace_affiliations.dateSeparator'/>
        	    <@orcid.msg 'workspace_affiliations.present'/>)
        	</span>
        	<span class="affiliation-date" ng-show="affiliation.startDate && affiliation.endDate">
        		(<span ng-show="affiliation.startDate.year">{{affiliation.startDate.year}}</span><span ng-show="affiliation.startDate.month">-{{affiliation.startDate.month}}</span>
        		<@orcid.msg 'workspace_affiliations.dateSeparator'/>
        		<span ng-show="affiliation.endDate.year">{{affiliation.endDate.year}}</span><span ng-show="affiliation.endDate.month">-{{affiliation.endDate.month}}</span>)
            </span>
            <span class="affiliation-date" ng-show="!affiliation.startDate && affiliation.endDate">
        	     (<span ng-show="affiliation.endDate.year">{{affiliation.endDate.year}}</span><span ng-show="affiliation.endDate.month">-{{affiliation.endDate.month}}</span>)
        	</span>
        </h3>
        <div class="affiliation-details" ng-show="affiliation.roleTitle">
            <span ng-bind-html="affiliation.roleTitle.value"></span>
        </div>
       </div>
       <!-- Privacy Settings -->
       <div class="col-md-3 col-sm-3 workspace-toolbar">       	
       	<#if !(isPublicProfile??)>
       		<a href ng-click="deleteAffiliation(affiliation)" class="glyphicon glyphicon-trash grey"></a>
       		<ul class="workspace-private-toolbar">
				<@orcid.privacyToggle  angularModel="affiliation.visibility.visibility"
				questionClick="toggleClickPrivacyHelp(affiliation.putCode.value)"
				clickedClassCheck="{'popover-help-container-show':privacyHelp[affiliation.putCode.value]==true}" 
				publicClick="setPrivacy(affiliation, 'PUBLIC', $event)" 
                  	limitedClick="setPrivacy(affiliation, 'LIMITED', $event)" 
                  	privateClick="setPrivacy(affiliation, 'PRIVATE', $event)" />			        
        	</ul>
        </#if>
	</div>
</div>
<div class="row" ng-show="moreInfo[affiliation.putCode.value]">
	<div class="col-md-8 col-sm-8 col-xs-12">
		<#include "affiliate_more_info_inc_v2.ftl"/>
	</div>
	<div class="col-md-4 col-sm-4 col-sm-12">
		<!-- Versions and validations are going to be here -->	
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
