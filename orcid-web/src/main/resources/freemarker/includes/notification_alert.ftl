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
<div ng-controller="NotificationAlertsCtrl">
	<div class="notification-alert" ng-repeat="notification in notificationsSrvc.notificationAlerts" ng-show="alert['$index'] == null" ng-cloak>		
		<h3 class="notification-alert-title" ng-cloak>		
			<@orcid.msg 'notifications.alert_title_1'/>
		</h3>
		<p>
			<@orcid.msg 'notifications.alert_content_1'/> {{notification.idpName}} <@orcid.msg 'notifications.alert_content_2'/> {{notification.source.sourceName.content}} <@orcid.msg 'notifications.alert_content_3'/>
		</p>	
		<div class="pull-right">
			<a ng-click="notificationsSrvc.archive(notification.putCode); alert['$index'] = !alert['$index']" class="cancel"><@orcid.msg 'notifications.archive'/></a>
			<a ng-click="notificationsSrvc.suppressAlert(notification.putCode); alert['$index'] = !alert['$index']" class="cancel"><@orcid.msg 'notifications.alert_close'/></a>
			<a ng-href="<@orcid.rootPath '/inbox'/>/{{notification.putCode}}/action?target={{notification.authorizationUrl.uri | uri}}" ng-click="notificationsSrvc.archive(notification.putCode); alert['$index'] = !alert['$index']" target="notifications.alert_link" class="btn btn-primary"><@orcid.msg 'notifications.alert_link'/></a>
		</div>	
	</div>
</div>