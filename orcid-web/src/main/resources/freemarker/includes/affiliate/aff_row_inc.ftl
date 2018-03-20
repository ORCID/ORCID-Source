<div class="row">        
  	<!-- Information -->
	<div class="col-md-8 col-sm-8">
	    <h3 class="affiliation-title">
        	<strong ng-bind-html="group.getActive().affiliationName.value"></strong>
        	<span class="affiliation-date" ng-show="group.getActive().startDate && !group.getActive().endDate">
        	    (<span ng-show="group.getActive().startDate.year">{{group.getActive().startDate.year}}</span><span ng-show="group.getActive().startDate.month">-{{group.getActive().startDate.month}}</span>
        	    <@orcid.msg 'workspace_affiliations.dateSeparator'/>
        	    <@orcid.msg 'workspace_affiliations.present'/>)
        	</span>
        	<span class="affiliation-date" ng-show="group.getActive().startDate && group.getActive().endDate">
        		(<span ng-show="group.getActive().startDate.year">{{group.getActive().startDate.year}}</span><span ng-show="group.getActive().startDate.month">-{{group.getActive().startDate.month}}</span>
        		<@orcid.msg 'workspace_affiliations.dateSeparator'/>
        		<span ng-show="group.getActive().endDate.year">{{group.getActive().endDate.year}}</span><span ng-show="group.getActive().endDate.month">-{{group.getActive().endDate.month}}</span>)
            </span>
            <span class="affiliation-date" ng-show="!group.getActive().startDate && group.getActive().endDate">
        	     (<span ng-show="group.getActive().endDate.year">{{group.getActive().endDate.year}}</span><span ng-show="group.getActive().endDate.month">-{{group.getActive().endDate.month}}</span>)
        	</span>
        </h3>
        <div class="affiliation-details" ng-show="group.getActive().roleTitle">
            <span ng-bind-html="group.getActive().roleTitle.value"></span>
        </div>
       </div>
       <!-- Privacy Settings -->
       <div class="col-md-4 col-sm-4 workspace-toolbar">
       	<#include "affiliate_more_info_inc.ftl"/>
       	<#if !(isPublicProfile??)>
       		<a href ng-click="deleteAffiliation(group.getActive())" class="glyphicon glyphicon-trash grey"></a>
       		<ul class="workspace-private-toolbar">
				<@orcid.privacyToggle  angularModel="group.getActive().visibility.visibility"
				questionClick="toggleClickPrivacyHelp(group.getActive().putCode.value)"
				clickedClassCheck="{'popover-help-container-show':privacyHelp[group.getActive().putCode.value]==true}" 
				publicClick="setPrivacy(group.getActive(), 'PUBLIC', $event)" 
                  	limitedClick="setPrivacy(group.getActive(), 'LIMITED', $event)" 
                  	privateClick="setPrivacy(group.getActive(), 'PRIVATE', $event)" />			        
        	</ul>
        </#if>
	</div>
</div>
