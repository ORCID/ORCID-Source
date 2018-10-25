<div>        	        	
    <p *ngIf="notification?.authorizationUrl">
        <@orcid.msg 'email.institutional_connection.1' /> {{notification?.idpName}} <@orcid.msg 'email.institutional_connection.2' /><a href="<@orcid.rootPath '/inbox'/>/{{notification?.putCode}}/action?target={{notification?.authorizationUrl?.uri | uri}}" target="email.institutional_connection.here"><@orcid.msg 'email.institutional_connection.here' /></a><@orcid.msg 'email.institutional_connection.3' /> {{notification?.source?.sourceName?.content}} <@orcid.msg 'email.institutional_connection.4' />
    </p>
    <p *ngIf="!notification?.authorizationUrl">
       <@orcid.msg 'email.institutional_connection.disabled.1' /> {{notification?.idpName}}<@orcid.msg 'email.institutional_connection.disabled.2' />
    </p>         
    <div class="pull-right margin-top">
        <button *ngIf="!notification?.archivedDate" (click)="archive(notification.putCode)" class="btn btn-white-no-border cancel-left"><@orcid.msg 'notifications.archive'/></button>
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