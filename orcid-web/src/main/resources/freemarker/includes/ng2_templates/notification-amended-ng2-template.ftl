<div>        	        	
	<p><strong>{{notification?.source?.sourceName?.content}}</strong> <@orcid.msg 'notifications.has_updated'/> {{notification?.amendedSection | replaceSeparatorWithSpace | titlecase}} <@orcid.msg 'notifications.section_of'/></p>
        <p *ngIf="notification?.activities">
            <div *ngFor="let activity of notification?.activities?.activities">
                 <strong>{{activity?.activityName}}</strong><span *ngIf="activity.externalIdentifier"> ({{activity?.externalIdentifier?.externalIdentifierType}}: {{activity?.externalIdentifier?.externalIdentifierId}})</span>
            </div>
        <p>
    <div class="pull-right margin-top">
		<button *ngIf="!notification?.archivedDate" (click)="archive(notification.putCode)" class="btn btn-white-no-border cancel-left"><@orcid.msg 'notifications.archive'/></button>  <a href="<@orcid.rootPath '/my-orcid'/>" target="_parent" class="btn btn-primary"><@orcid.msg 'notifications.view_on_your_record'/></a>
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