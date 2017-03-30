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
<html>
<#import "/macros/orcid.ftl" as orcid />
<#assign verDateTime = startupDate?datetime>
<#assign ver="${verDateTime?iso_utc}">
<#include "/common/html-head.ftl" />
<head>
	<meta charset="utf-8" />    
    <meta name="description" content="">
    <meta name="author" content="ORCID">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
    <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.28/angular.min.js"></script>
    <link rel="stylesheet" href="${staticCdn}/twitter-bootstrap/3.1.0/css/bootstrap.min.css?v=${ver}"/>
    <link rel="stylesheet" href="${staticLoc}/css/fonts.css?v=${ver}"/>
    <link rel="stylesheet" href="${staticCdn}/css/orcid.new.css?v=${ver}"/>
	<style>
		body, html{			
			color: #494A4C;
			font-size: 15px;
			font-family: 'Gill Sans W02', 'Helvetica', sans-serif;
			font-style: normal;
			min-height: 100px; /* Do not change */
			height: auto; /* Do not change */
			padding-bottom: 30px; /* Do not change */
		}		
		
		.margin-top{
			margin-top: 15px;
			clear: both;			
		}
		
		.margin-top .btn-primary{
			margin-left: 15px;
		}
	</style>
	<script type="text/javascript">
		var appInIframe = angular.module('appInFrame', []);
		
		appInIframe.factory('$parentScope', function($window) {
		  return $window.parent.angular.element($window.frameElement).scope();
		});
	
		appInIframe.controller('iframeController', function($scope, $parentScope) {
	      
		  $scope.putCode = ${notification.putCode?c};
		  $scope.archivedDate = "${notification.archivedDate!}"		  
		  
		  $scope.archive = function(id) {			
			$parentScope.archive(id);
			$parentScope.$apply();
		  };		  
		});
	</script>
	<!--  Do not remove -->
	<script type="text/javascript" src="${staticCdn}/javascript/iframeResizer.contentWindow.min.js?v=${ver}"></script>
</head>
<body data-baseurl="<@orcid.rootPath '/'/>" ng-app="appInFrame" ng-controller="iframeController"> 
    <div>        	        	
    	<p><strong>${notification.source.sourceName.content}</strong> has updated items in the ${notification.amendedSection!?capitalize} section of your record.</p>
        <#if (notification.activities.activities)??>
            <p>
                <#list notification.activities.activities as activity>
                     <div><strong>${activity.activityName}</strong><#if activity.externalIdentifier??> (${activity.externalIdentifier.externalIdentifierType}: ${activity.externalIdentifier.externalIdentifierId})</#if></div>
                </#list>
            <p>
        </#if>
        <div class="pull-right margin-top">
    		<a ng-click="archive(putCode)" target="_parent" ng-hide="archivedDate" class="">Archive</a>  <a href="<@orcid.rootPath '/my-orcid'/>" target="_parent" class="btn btn-primary">View on your record</a>
    	</div>
     </div>
     <#if notification.sourceDescription??>
         <div class="margin-top">
             <strong>About ${notification.source.sourceName.content}</strong>
         </div>
         <div>
             ${notification.sourceDescription}
         </div>
     </#if>
 </body>
 </html>