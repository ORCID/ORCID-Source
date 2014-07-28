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
<#-- @ftlvariable name="profile" type="org.orcid.jaxb.model.message.OrcidProfile" -->
<@protected nav="record">
<#escape x as x?html>
<div class="col-md-3 lhs left-aside">
	<div class="workspace-profile">
		<#include "includes/id_banner.ftl"/>
	</div>
</div>
<div class="col-md-9 right-aside">
	<h1>Notifications</h1>
	<div ng-controller="NotificationsCtrl">
		<div ng-repeat="notification in notifications">
			{{notification.subject}}
		</div>
	</div>
</div>
</#escape>
</@protected>