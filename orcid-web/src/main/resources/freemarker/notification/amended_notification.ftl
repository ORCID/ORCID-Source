<html>
<#import "/macros/orcid.ftl" as orcid />

<#include "/common/html-head.ftl" />
<head>
	<meta charset="utf-8" />    
    <meta name="description" content="">
    <meta name="author" content="ORCID">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
    <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.28/angular.min.js"></script>
    <link rel="stylesheet" href="${staticCdn}/twitter-bootstrap/3.1.0/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="${staticCdn}/css/orcid.new.css"/>
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
	<script type="text/javascript" src="${staticCdn}/javascript/iframeResizer.contentWindow.min.js"></script>
</head>
<body data-baseurl="<@orcid.rootPath '/'/>" ng-app="appInFrame" ng-controller="iframeController"> 
    <div>        	        	
    	<p><strong>${notification.source.sourceName.content}</strong> <@orcid.msg 'notifications.has_updated'/> ${notification.amendedSection!?capitalize} <@orcid.msg 'notifications.section_of'/></p>
        <#if (notification.activities.activities)??>
            <p>
                <#list notification.activities.activities as activity>
                     <div><strong>${activity.activityName}</strong><#if activity.externalIdentifier??> (${activity.externalIdentifier.externalIdentifierType}: ${activity.externalIdentifier.externalIdentifierId})</#if></div>
                </#list>
            <p>
        </#if>
        <div class="pull-right margin-top">
    		<a ng-click="archive(putCode)" target="_parent" ng-hide="archivedDate" class=""><@orcid.msg 'notifications.archive'/></a>  <a href="<@orcid.rootPath '/my-orcid'/>" target="_parent" class="btn btn-primary"><@orcid.msg 'notifications.view_on_your_record'/></a>
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