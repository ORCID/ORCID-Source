<script type="text/ng-template" id="notification-add-activities-ng2-template">
	<div *ngIf="notification?.notificationIntro">
	    {{notification?.notificationIntro}}
	</div>
	<div *ngIf="!notification?.notificationIntro">
	        <strong>{{notification?.source?.sourceName?.content}}</strong> <@orcid.msg 'notifications.would_add'/>
	</div>
	<div class="notifications-inner">
		<!-- Education -->
		<div *ngIf="educationsCount > 0">
			<div class="workspace-accordion-header">
				<i class="glyphicon-chevron-down glyphicon x075"></i> <@orcid.msg 'notifications.education_label'/> ({{educationsCount}})
			</div>
			<strong>{{educationsList}}</strong>
		</div>
		<!-- Employment -->
		<div *ngIf="employmentsCount > 0">
			<div class="workspace-accordion-header">
				<i class="glyphicon-chevron-down glyphicon x075"></i> <@orcid.msg 'notifications.employment_label'/> ({{employmentsCount}})
			</div>
			<strong>{{employmentsList}}</strong>
		</div>
		<!-- Funding -->
		<div *ngIf="fundingsCount > 0">
			<div class="workspace-accordion-header">
				<i class="glyphicon-chevron-down glyphicon x075"></i> <@orcid.msg 'notifications.fundings_label'/> ({{fundingsCount}})
			</div>
			<strong>{{fundingsList}}</strong>
			<div *ngIf="notification?.authorizationUrl?.uri">
				<div class="margin-top">
					<strong>{{notification?.source?.sourceName?.content}}</strong> <@orcid.msg 'notifications.would_permission'/>
				</div>
				<div class="margin-top">
					<button *ngIf="!notification?.archivedDate" (click)="archive(notification.putCode)" class="btn btn-white-no-border cancel-left"><@orcid.msg 'notifications.archivewithoutgranting' /></button> <a class="btn btn-primary" href="<@orcid.rootPath '/inbox'/>/{{notification.putCode}}/action?target={{notification.authorizationUrl.uri | uri}}" target="notifications.grant_permissions"><span class="glyphicons cloud-upload"></span> <@orcid.msg 'notifications.grant_permissions'/></a>
				</div>
			</div>
		</div>
		<!-- Peer Review -->
		<div *ngIf="peerReviewsCount > 0">
			<div class="workspace-accordion-header">
			</div>
				<i class="glyphicon-chevron-down glyphicon x075"></i> <@orcid.msg 'notifications.peer_review_label'/> ({{peerReviewsCount}})
			<strong>{{peerReviewsList}}</strong>
			<div *ngIf="notification?.authorizationUrl?.uri">
				<div class="margin-top">
					<strong>{{notification?.source?.sourceName?.content}}</strong> <@orcid.msg 'notifications.would_permission' />
				</div>
				<div class="margin-top">
					<button *ngIf="!notification?.archivedDate" (click)="archive(notification.putCode)" class="btn btn-white-no-border cancel-left"><@orcid.msg 'notifications.archivewithoutgranting' /></button> <a class="btn btn-primary" href="<@orcid.rootPath '/inbox'/>/{{notification.putCode}}/action?target={{notification.authorizationUrl.uri | uri}}" target="notifications.grant_permissions"><span class="glyphicons cloud-upload"></span> <@orcid.msg 'notifications.grant_permissions'/></a>
				</div>								
			</div>
		</div>
		<!-- Works -->
		<div *ngIf="worksCount > 0">
			<div class="workspace-accordion-header">
				<i class="glyphicon-chevron-down glyphicon x075"></i> <@orcid.msg 'notifications.works_label' /> ({{worksCount}})
			</div>			
			<strong>{{worksList}}</strong>			
			<div *ngIf="notification?.authorizationUrl?.uri">
				<div class="margin-top">
					<strong>{{notification?.source?.sourceName?.content}}</strong> <@orcid.msg 'notifications.would_permission' />
				</div>
				<div class="margin-top pull-right">
					<button *ngIf="!notification?.archivedDate" (click)="archive(notification.putCode)" class="btn btn-white-no-border cancel-left"><@orcid.msg 'notifications.archivewithoutgranting' /></button> <a class="btn btn-primary" href="<@orcid.rootPath '/inbox'/>/{{notification.putCode}}/action?target={{notification.authorizationUrl.uri | uri}}" target="notifications.grant_permissions"><span class="glyphicons cloud-upload"></span> <@orcid.msg 'notifications.grant_permissions'/></a>
				</div>		
			</div>
		</div>
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
		<small><@orcid.msg 'notifications.agreement_advice'/> <a href="<@orcid.msg 'common.kb_uri_default'/>360006894514" target="notifications.learn_more"><@orcid.msg 'notifications.learn_more'/></a>
		</small>
	</div>
</script>