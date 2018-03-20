<script type="text/ng-template" id="notifications-alert-ng2-template">
    <div>
        <div class="notification-alert" *ngFor="let notification of  notificationsSrvc.notificationAlerts" *ngIf="alert['$index'] == null" >        
            <h3 class="notification-alert-title" >      
                <@orcid.msg 'notifications.alert_title_1'/>
            </h3>
            <p>
                <@orcid.msg 'notifications.alert_content_1'/> {{notification.idpName}} <@orcid.msg 'notifications.alert_content_2'/> {{notification.source.sourceName.content}} <@orcid.msg 'notifications.alert_content_3'/>
            </p>    
            <div class="pull-right">
                <a (click)="notificationsSrvc.archive(notification.putCode); alert['$index'] = !alert['$index']" class="cancel"><@orcid.msg 'notifications.archive'/></a>
                <a (click)="notificationsSrvc.suppressAlert(notification.putCode); alert['$index'] = !alert['$index']" class="cancel"><@orcid.msg 'notifications.alert_close'/></a>
                <a href="<@orcid.rootPath '/inbox'/>/{{notification.putCode}}/action?target={{notification.authorizationUrl.uri | uri}}" (click)="notificationsSrvc.archive(notification.putCode); alert['$index'] = !alert['$index']" target="notifications.alert_link" class="btn btn-primary"><@orcid.msg 'notifications.alert_link'/></a>
            </div>  
        </div>
    </div>
</script>