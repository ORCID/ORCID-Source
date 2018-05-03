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
    ${body}
 </body>
 </html>