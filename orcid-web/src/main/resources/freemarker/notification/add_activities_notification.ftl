<!DOCTYPE html>
<html>
<#import "/macros/orcid.ftl" as orcid />

<#assign aworks = 0>
<#assign tworks = "">
<#assign wbuttons = false>
<#assign wUrl = "">
<#assign wPutCode = "">

<#assign aeducation = 0>
<#assign teducation = "">
<#assign edubuttons = false>
<#assign eduUrl = "">
<#assign eduPutCode = "">

<#assign aemployment = 0>
<#assign temployment = "">
<#assign empButtons = false>
<#assign empUrl = "">
<#assign empPutCode = "">

<#assign apeerreview = 0>
<#assign tpeerreview = "">
<#assign pButtons = false>
<#assign pUrl = "">
<#assign pPutCode = "">

<#assign afunding = 0>
<#assign tfunding = "">
<#assign fButtons = false>
<#assign fUrl = "">
<#assign fPutCode = "">


<head>
    <meta charset="utf-8" />    
    <meta name="description" content="">
    <meta name="author" content="ORCID">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
    <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.28/angular.min.js"></script>
    <link rel="stylesheet" href="${staticCdn}/twitter-bootstrap/3.1.0/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="${staticLoc}/css/glyphicons.css"/>
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
				
		.glyphicons{
			top: -10px;
			padding-left: 21px;
			
		}
		
		.glyphicons:before{
			color: #FFF;
			font: 16px/1em 'Glyphicons Regular'
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
	
	<#list notification.items.items?sort_by("itemType") as activity>
		<#switch activity.itemType>
			 <#case "WORK">
			  	<#assign aworks = aworks + 1>
			  	<#assign tworks = tworks + activity.itemName>
			  	<#if activity.externalId??>
	           		<#assign tworks = tworks + "(" + activity.externalId.externalIdType + ":" + activity.externalId.externalIdValue + ")">
	       		</#if>
	       		<#assign tworks = tworks + "<br/>">
	       		<#if notification.authorizationUrl??>
	       			<#assign wbuttons = true>
	       			<#assign wUrl = notification.authorizationUrl.uri>
	       			<#assign wPutCode = notification.putCode>
	       		</#if>
			    <#break>
			  <#case "EMPLOYMENT">
			     <#assign aemployment = aemployment + 1>
			     <#assign temployment = temployment + activity.itemName + "<br/>">
			     <#break>
			  <#case "EDUCATION">
			     <#assign aeducation = aeducation + 1>
			     <#assign teducation = teducation + activity.itemName + "<br/>">
			     <#break>
			 <#case "FUNDING">
			     <#assign afunding = afunding + 1>
			     <#assign tfunding = tfunding + activity.itemName>
			     <#if activity.externalId??>
	           		<#assign tfunding = tfunding + "(" + activity.externalId.externalIdType + ":" + activity.externalId.externalIdValue + ")">
	       		 </#if>
	       		 <#assign tfunding = tfunding + "<br/>">
			     <#break>
			 <#case "PEER_REVIEW">
			     <#assign apeerreview = apeerreview + 1>
			     <#assign tpeerreview = tpeerreview + activity.itemName>
			     <#if activity.externalId??>
	           		<#assign tpeerreview = tpeerreview + "(" + activity.externalId.externalIdType + ":" + activity.externalId.externalIdValue + ")">
	       		 </#if>
	       		 <#assign tpeerreview = tpeerreview + "<br/>">
			     <#break>
			  <#default>
		</#switch>
		<#if activity.externalId??>
	           (${activity.externalId.externalIdType}: ${activity.externalId.externalIdValue})
	       </#if>
	</#list>


    <!-- Start rendering -->
    <div>
        <#if notification.notificationIntro??>
            ${notification.notificationIntro}
        <#else>
            <strong>${notification.source.sourceName.content}</strong> <@orcid.msg 'notifications.would_add'/>
        </#if>
    </div>
	<div class="notifications-inner">
		<#if aeducation gt 0>
			<!-- Education -->
			<div class="workspace-accordion-header">
				<i class="glyphicon-chevron-down glyphicon x075"></i> <@orcid.msg 'notifications.education_label'/> (${aeducation})
			</div>
			<strong>${teducation}</strong>
		</#if>
		<#if aemployment gt 0>
			<!-- Employment -->
			<div class="workspace-accordion-header">
				<i class="glyphicon-chevron-down glyphicon x075"></i> <@orcid.msg 'notifications.employment_label'/> (${aemployment})
			</div>
			<strong>${temployment}</strong>
		</#if>
		<#if afunding gt 0>
			<!-- Funding -->
			<div class="workspace-accordion-header">
				<i class="glyphicon-chevron-down glyphicon x075"></i> <@orcid.msg 'notifications.fundings_label'/> (${afunding})
			</div>
			<strong>${tfunding}</strong>
			<#if fButtons>
				<div class="margin-top">
					<strong>${notification.source.sourceName.content}</strong> <@orcid.msg 'notifications.would_permission'/>
				</div>
				<div class="margin-top">
					<a href="" ng-click="archive('${fPutCode?c}')" type="reset" ng-hide="archivedDate"><@orcid.msg 'notifications.archivewithoutgranting' /></a>  <a class="btn btn-primary" href="<@orcid.rootPath '/inbox'/>/${fPutCode?c}/action?target=${fUrl?url}" target="notifications.grant_permissions"><span class="glyphicons cloud-upload"></span> <@orcid.msg 'notifications.grant_permissions'/></a>
				</div>
			</#if>
		</#if>
		<#if apeerreview gt 0>
			<!-- Peer Review -->
			<div class="workspace-accordion-header">
			</div>
				<i class="glyphicon-chevron-down glyphicon x075"></i> <@orcid.msg 'notifications.peer_review_label'/> (${apeerreview})
			<strong>${tpeerreview}</strong>
			<#if pButtons>
				<div class="margin-top">
					<strong>${notification.source.sourceName.content}</strong> <@orcid.msg 'notifications.would_permission' />
				</div>
				<div class="margin-top">
					<a href="" ng-click="archive('${pPutCode?c}')" type="reset" ng-hide="archivedDate"><@orcid.msg 'notifications.archivewithoutgranting' /></a>  <a class="btn btn-primary" href="<@orcid.rootPath '/inbox'/>/${pPutCode?c}/action?target=${pUrl?url}" target="notifications.grant_permissions"><span class="glyphicons cloud-upload"></span> <@orcid.msg 'notifications.grant_permissions'/></a>
				</div>								
			</#if>
		</#if>
		<#if aworks gt 0>
			<!-- Works -->
			<div class="workspace-accordion-header">
				<i class="glyphicon-chevron-down glyphicon x075"></i> <@orcid.msg 'notifications.works_label' /> (${aworks})
			</div>			
			<strong>${tworks}</strong>			
			<#if wbuttons>
				<div class="margin-top">
					<strong>${notification.source.sourceName.content}</strong> <@orcid.msg 'notifications.would_permission' />
				</div>
				<div class="margin-top pull-right">
					<a href="" ng-click="archive('${wPutCode?c}')" type="reset" ng-hide="archivedDate"><@orcid.msg 'notifications.archivewithoutgranting' /></a><a class="btn btn-primary" href="<@orcid.rootPath '/inbox'/>/${wPutCode?c}/action?target=${wUrl?url}" target="notifications.grant_permissions"><span class="glyphicons cloud-upload"></span> <@orcid.msg 'notifications.grant_permissions'/></a>
				</div>		
			</#if>
		</#if>
	</div>
	<#if notification.sourceDescription??>
        <div class="margin-top">
            <strong><@orcid.msg 'notifications.about' /> ${notification.source.sourceName.content}</strong>
        </div>
        <div>
            ${notification.sourceDescription}
        </div>
    </#if>
    <div class="margin-top">
    	<small><@orcid.msg 'notifications.agreement_advice'/> <a href="${knowledgeBaseUri}/articles/1807645-notifications-preferences" target="notifications.learn_more"><@orcid.msg 'notifications.learn_more'/></a></small>
    </div>
</body>
</html>