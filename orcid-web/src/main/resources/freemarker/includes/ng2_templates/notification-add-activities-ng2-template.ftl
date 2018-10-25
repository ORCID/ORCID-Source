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

<div *ngFor="let activity of notification.items.items | orderBy:itemType">
	<div *ngIf="activity.itemType=='WORK">
	</div>
</div>

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

<div *ngIf="notification?.notificationIntro">
    {{notification.notificationIntro}}
</div>
<div *ngIf="!notification?.notificationIntro">
        <strong>{{notification?.source?.sourceName?.content}}</strong> <@orcid.msg 'notifications.would_add'/>
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
				<button *ngIf="!notification?.archivedDate" (click)="archive(notification.putCode)" class="btn btn-white-no-border cancel-left"><@orcid.msg 'notifications.archivewithoutgranting' /></button> <a class="btn btn-primary" href="<@orcid.rootPath '/inbox'/>/${fPutCode?c}/action?target=${fUrl?url}" target="notifications.grant_permissions"><span class="glyphicons cloud-upload"></span> <@orcid.msg 'notifications.grant_permissions'/></a>
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
				<strong>{{notification?.source?.sourceName?.content}}</strong> <@orcid.msg 'notifications.would_permission' />
			</div>
			<div class="margin-top">
				<button *ngIf="!notification?.archivedDate" (click)="archive(notification.putCode)" class="btn btn-white-no-border cancel-left"><@orcid.msg 'notifications.archivewithoutgranting' /></button> <a class="btn btn-primary" href="<@orcid.rootPath '/inbox'/>/${pPutCode?c}/action?target=${pUrl?url}" target="notifications.grant_permissions"><span class="glyphicons cloud-upload"></span> <@orcid.msg 'notifications.grant_permissions'/></a>
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
				<strong>{{notification?.source?.sourceName?.content}}</strong> <@orcid.msg 'notifications.would_permission' />
			</div>
			<div class="margin-top pull-right">
				<button *ngIf="!notification?.archivedDate" (click)="archive(notification.putCode)" class="btn btn-white-no-border cancel-left"><@orcid.msg 'notifications.archivewithoutgranting' /></button> <a class="btn btn-primary" href="<@orcid.rootPath '/inbox'/>/${wPutCode?c}/action?target=${wUrl?url}" target="notifications.grant_permissions"><span class="glyphicons cloud-upload"></span> <@orcid.msg 'notifications.grant_permissions'/></a>
			</div>		
		</#if>
	</#if>
</div>
<div *ngIf="notification?.sourceDescription">
     <div class="margin-top">
         <strong><@orcid.msg 'notifications.about' /> {{notification?.source?.sourceName?.content}}</strong>
     </div>
     <div>
         {{notification?.sourceDescription}}
     </div>
</div>
<div class="margin-top">
	<small><@orcid.msg 'notifications.agreement_advice'/> <a href="<@orcid.msg 'common.kb_uri_default'/>360006894514" target="notifications.learn_more"><@orcid.msg 'notifications.learn_more'/></a></small>
</div>