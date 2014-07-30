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
<@protected nav="notifications">
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
			<div ng-cloak>
				<a href="" ng-click="toggleDisplayBody(notification.putCode.path)" class="toggle-text">
					<i class="glyphicon-chevron-down glyphicon x0" ng-class="{'glyphicon-chevron-right':!displayBody[notification.putCode.path]}"></i></a>
				</a>
				<strong>{{notification.subject}}</strong> from <strong ng-show="notification.source">{{notification.source.sourceName}}</strong><strong ng-hide="notification.source">ORCID</strong> at <strong>{{notification.createdDate|date:'yyyy-MM-ddTHH:mm'}}</strong>
				<i ng-hide="notification.readDate" class="glyphicon glyphicon-bell"></i>
			</div>
			<iframe ng-show="displayBody[notification.putCode.path]" ng-src="{{ '<@spring.url '/notifications'/>/' + notification.putCode.path + '/notification.html'}}" frameborder="0" width="100%" height="300"></iframe>
			<hr></hr>
		</div>
	</div>
</div>
</#escape>
</@protected>