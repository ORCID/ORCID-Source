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
<!DOCTYPE html>
<html>
<#assign verDateTime = startupDate?datetime>
<#assign ver="${verDateTime?iso_utc}">
<head>
    <meta charset="utf-8" />    
    <meta name="description" content="">
    <meta name="author" content="ORCID">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
    <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.28/angular.min.js"></script>
    <link rel="stylesheet" href="${staticCdn}/twitter-bootstrap/3.1.0/css/bootstrap.min.css?v=${ver}"/>
    <link rel="stylesheet" href="${staticCdn}/css/fonts.css?v=${ver}"/>
    <link rel="stylesheet" href="${staticCdn}/css/orcid.new.css?v=${ver}"/>
    <style> 
		body, html{			
			color: #494A4C;
			font-size: 15px;
			font-family: 'Gill Sans W02', 'Helvetica', sans-serif;
			font-style: normal;   
		}
		
		.workspace-accordion-header{
			color: #FFF;
			font-weight: bold;
			padding: 5px;
			margin: 10px 0;
			cursor: pointer;
		}
		
		.notifications-inner{
			padding: 5px 15px;			
		}
		
		.notifications-buttons{
			margin-top: 15px;
		}
		
		
	</style>
	<script type="text/javascript">
		var appInIframe = angular.module('appInFrame', []);
	
		appInIframe.factory('$parentScope', function($window) {
		  return $window.parent.angular.element($window.frameElement).scope();
		});
	
		appInIframe.controller('iframeController', function($scope, $parentScope) {
		  $scope.archive = function(id) {			
			$parentScope.archive(id);
			$parentScope.$apply();
		  };		  
		});
	</script>
</head>
<body data-baseurl="<@spring.url '/'/>" ng-app="appInFrame" ng-controller="iframeController">
	<div>
	    <strong>${notification.source.sourceName.content}</strong> would like to add the following items to your record:
	</div>
	<div class="notifications-inner">
		<#list notification.activities.activities as activity>
			<div class="workspace-accordion-header">
				<i class="glyphicon-chevron-down glyphicon x075"></i> ${activity.activityType?capitalize} (${notification.activities.activities?size})
			</div>	        		
        	<strong>${activity.activityName}</strong><br /> 
	        <#if activity.externalId??>
	            (${activity.externalId.externalIdType}: ${activity.externalId.externalIdValue})
	        </#if>
		</#list>
		<div class="notifications-buttons">
			<#if notification.authorizationUrl??>	
				<a class="btn btn-primary" href="${notification.authorizationUrl.uri}" target="_blank">
					<span class="glyphicons glyphicons-cloud-upload"></span>Add now
				</a>  
				<a class="btn btn-default" href="" ng-click="archive('${notification.putCode?c}')" type="reset">
					Archive
				</a>                                                     
			</#if>
		</div>
	</div>	
	<!--  Review it -->
	<script type="text/javascript" src="${staticCdn}/javascript/iframeResizer.contentWindow.min.js?v=${ver}"></script><!-- required for iframe resizing -->
</body>
</html>