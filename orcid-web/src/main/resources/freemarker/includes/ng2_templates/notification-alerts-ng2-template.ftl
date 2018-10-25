<script type="text/ng-template" id="notification-alerts-ng2-template">
    <div>
        <ng-container *ngFor="let notification of  notificationsSrvc.notificationAlerts; let index = index">
            <div *ngIf="alert['index']" class="notification-alert">        
                <h3 class="notification-alert-title" >      
                    <@orcid.msg 'notifications.alert_title_1'/>
                </h3>
                <p>
                    <@orcid.msg 'notifications.alert_content_1'/> {{notification.idpName}} <@orcid.msg 'notifications.alert_content_2'/> {{notification.source.sourceName.content}} <@orcid.msg 'notifications.alert_content_3'/>
                </p>    
                <div class="pull-right">
                    <a (click)="archive(notification.putCode); alert['index'] = !alert['index']" class="cancel"><@orcid.msg 'notifications.archive'/></a>
                    <a (click)="suppressAlert(notification.putCode)" class="cancel"><@orcid.msg 'notifications.alert_close'/></a>
                    <a href="<@orcid.rootPath '/inbox'/>/{{notification.putCode}}/action?target={{notification.source.sourceClientId.uri | uri}}" (click)="archive(notification.putCode); alert['index'] = !alert['index']" target="notifications.alert_link" class="btn btn-primary"><@orcid.msg 'notifications.alert_link'/></a>
                </div>  
            </div>
        </ng-container>
    </div>
</script>