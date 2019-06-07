<script type="text/ng-template" id="notification-body-ng2-template">
	<div class="notification">
		<div class="notifications-inner">
			<!--AMENDED-->
			<div *ngIf="notification.notificationType=='AMENDED'">
			    <div *ngIf="TOGGLZ_VERBOSE_NOTIFICATIONS; else defaultNotifications">
    			

    			    <p><strong>{{notification?.source?.sourceName?.content}}</strong> has updated your record:</p>
                    
    			    <ng-container *ngIf="addedWorksList != ''">
    			        <p>He have added the following elements:</p>
                        <div [innerHTML]="addedWorksList"></div>
    			    </ng-container>
    			    
    			    <ng-container *ngIf="addedWorksList != ''">
                        <p>He have updated the following elements:</p>
                        <div [innerHTML]="updatedWorksList"></div>
                    </ng-container>
                
                    <ng-container *ngIf="addedWorksList != ''">
                        <p>He have deleted the following elements:</p>
                        <div [innerHTML]="deletedWorksList"></div>
                    </ng-container>
    			    
    			    <div class="pull-right topBuffer">
                        <button *ngIf="!notification?.archivedDate" (click)="archive(notification.putCode)" class="btn btn-white-no-border cancel-left"><@orcid.msg 'notifications.archive'/></button> <a href="{{getBaseUri()}}/my-orcid" target="_parent" class="btn btn-primary"><@orcid.msg 'notifications.view_on_your_record'/></a>
                    </div>
			    
			    
    			</div>
    			<ng-template #defaultNotifications>
    			    <div>
                        <p><strong>{{notification?.source?.sourceName?.content}}</strong> <@orcid.msg 'notifications.has_updated'/> {{notification?.amendedSection | replaceSeparatorWithSpace | titlecase}} <@orcid.msg 'notifications.section_of'/></p>
                        <div class="pull-right topBuffer">
                            <button *ngIf="!notification?.archivedDate" (click)="archive(notification.putCode)" class="btn btn-white-no-border cancel-left"><@orcid.msg 'notifications.archive'/></button> <a href="{{getBaseUri()}}/my-orcid" target="_parent" class="btn btn-primary"><@orcid.msg 'notifications.view_on_your_record'/></a>
                        </div>
                    </div>
                </ng-template>
			</div>			
			
			<!--CUSTOM-->
			<div *ngIf="notification.notificationType=='ADMINISTRATIVE' || notification.notificationType=='CUSTOM' || notification.notificationType=='SERVICE_ANNOUNCEMENT' || notification.notificationType=='TIP'" [innerHTML]="notification.bodyHtml | extractContentFromBody">
		    </div>
		    <!--INSTITUTIONAL_CONNECTION-->
		    <div *ngIf="notification.notificationType=='INSTITUTIONAL_CONNECTION'">
		    	<p *ngIf="notification?.authorizationUrl">
		        <@orcid.msg 'email.institutional_connection.1' /> {{notification?.idpName}} <@orcid.msg 'email.institutional_connection.2' /><a href="{{getBaseUri()}}/inbox/{{notification?.putCode}}/action?target={{encodedUrl}}" target="email.institutional_connection.here"><@orcid.msg 'email.institutional_connection.here' /></a><@orcid.msg 'email.institutional_connection.3' /> {{notification?.source?.sourceName?.content}} <@orcid.msg 'email.institutional_connection.4' />
			    </p>
			    <p *ngIf="!notification?.authorizationUrl">
			       <@orcid.msg 'email.institutional_connection.disabled.1' /> {{notification?.idpName}}<@orcid.msg 'email.institutional_connection.disabled.2' />
			    </p>         
			    <div class="pull-right margin-top">
			        <button *ngIf="!notification?.archivedDate" (click)="archive(notification.putCode)" class="btn btn-white-no-border cancel-left"><@orcid.msg 'notifications.archive'/></button>
				</div>
		    </div>
		    <!--PERMISSION-->
			<div *ngIf="notification.notificationType=='PERMISSION'">
				<div *ngIf="notification?.notificationIntro">
				    {{notification?.notificationIntro}}
				</div>
				<div *ngIf="!notification?.notificationIntro">
				        <strong>{{notification?.source?.sourceName?.content}}</strong> <@orcid.msg 'notifications.would_add'/>
				</div>
				<!-- Education -->
				<div *ngIf="educationsCount > 0">
					<div class="workspace-accordion-header">
						<@orcid.msg 'notifications.education_label'/> ({{educationsCount}})
					</div>
					<span [innerHTML]="educationsList"></span>
				</div>
				<div *ngIf="employmentsCount > 0">
					<div class="workspace-accordion-header">
						<@orcid.msg 'notifications.employment_label'/> ({{employmentsCount}})
					</div>
					<span [innerHTML]="employmentsList"></span>
				</div>
				<!-- Funding -->
				<div *ngIf="fundingsCount > 0">
					<div class="workspace-accordion-header">
						<@orcid.msg 'notifications.fundings_label'/> ({{fundingsCount}})
					</div>
					<span [innerHTML]="fundingsList"></span>
				</div>
				<div *ngIf="peerReviewsCount > 0">
					<div class="workspace-accordion-header">
					</div>
						<@orcid.msg 'notifications.peer_review_label'/> ({{peerReviewsCount}})
					<span [innerHTML]="peerReviewsList"></span>
				</div>
				<div *ngIf="worksCount > 0">
					<div class="workspace-accordion-header">
						<@orcid.msg 'notifications.works_label' /> ({{worksCount}})
					</div>			
					<span [innerHTML]="worksList"></span>			
				</div>
				<div *ngIf="notification?.authorizationUrl">
					<div class="topBuffer">
						<strong>{{notification?.source?.sourceName?.content}}</strong> <@orcid.msg 'notifications.would_permission' />
					</div>
					<div class="topBuffer pull-right">
						<button *ngIf="!notification?.archivedDate" (click)="archive(notification.putCode)" class="btn btn-white-no-border cancel-left"><@orcid.msg 'notifications.archivewithoutgranting' /></button> <a class="btn btn-primary" href="{{getBaseUri()}}/inbox/{{notification.putCode}}/action?target={{encodedUrl}}" target="notifications.grant_permissions"><span class="glyphicons cloud-upload"></span> <@orcid.msg 'notifications.grant_permissions'/></a>
					</div>		
				</div>
			</div>
			<div *ngIf="notification?.sourceDescription" class="topBuffer clear-fix">
			    <p><strong><@orcid.msg 'notifications.about' /> {{notification?.source?.sourceName?.content}}</strong><br>
			        {{notification?.sourceDescription}}
			    </p>
			</div>
			<div class="topBuffer">
				<small><@orcid.msg 'notifications.agreement_advice'/> <a href="<@orcid.msg 'common.kb_uri_default'/>360006972953" target="notifications.learn_more"><@orcid.msg 'notifications.learn_more'/></a>
				</small>
			</div>
		</div>
	</div>
</script>