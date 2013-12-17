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

<#include "includes/affiliate/del_grant_inc.ftl"/>

<#include "includes/grant/add_grant_inc.ftl"/>
<div ng-controller="GrantCtrl">
	<!-- Grants -->
	<div id="workspace-grants" class="workspace-accordion-item workspace-accordion-active" >
		<div class="workspace-accordion-header"><a name='workspace-grants' />
		    <a href="" ng-click="workspaceSrvc.toggleGrants()" class="toggle-text">
		  		<i class="glyphicon-chevron-down glyphicon x0" ng-class="{'glyphicon-chevron-right':workspaceSrvc.displayGrants==false}"></i></a>
		   	</a> 
		    <a href="" ng-click="workspaceSrvc.toggleGrants()" class="toggle-text"><@orcid.msg 'org.orcid.jaxb.model.message.GrantType.grant'/></a>
			<a href="" class="label btn-primary" ng-click="addGrantModal()"><@orcid.msg 'manual_grant_form_contents.add_grant_manually'/></a>
		</div>
		<div ng-show="workspaceSrvc.displayGrants" class="workspace-accordion-content">
			<#include "includes/grant/body_grant_inc.ftl" />
		</div>
	</div>		
</div>