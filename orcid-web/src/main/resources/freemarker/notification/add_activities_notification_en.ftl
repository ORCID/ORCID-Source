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
    <title>${title!"ORCID"}</title>
    <meta name="description" content="">
    <meta name="author" content="ORCID">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
    <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.28/angular.min.js"></script>
    <link rel="stylesheet" href="${staticCdn}/twitter-bootstrap/3.1.0/css/bootstrap.min.css?v=${ver}"/>
    <link rel="stylesheet" href="${staticCdn}/css/orcid.new.css?v=${ver}"/>
    <style>
	body, html{
		height: auto;
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
    ${notification.source.sourceName.content} has ${notification.activities.activities?size}
    <#if notification.activities.activities?size == 1>
        item
    <#else>
        items
    </#if>
    to add to your record.
</div>
<div>
	<ul>
		<#list notification.activities.activities as activity>
    		<li style="padding:4px;">    		
	        	${activity.activityName} (${activity.activityType?capitalize}) 
		        <#if activity.externalId??>
		            (${activity.externalId.externalIdType}: ${activity.externalId.externalIdValue})
		        </#if>
	        </li>
		</#list>
	</ul>
</div>

<#if notification.authorizationUrl??>	
	<a class="btn btn-primary" href="${notification.authorizationUrl.uri}" target="_blank">
		Add items
	</a>  
	<a class="btn btn-default" href="" ng-click="archive('${notification.putCode?c}')" type="reset">
		Archive
	</a>                                                     
</#if>
<script type="text/javascript" src="${staticCdn}/javascript/iframeResizer.contentWindow.min.js?v=${ver}"></script><!-- required for iframe resizing -->
</body>
</html>