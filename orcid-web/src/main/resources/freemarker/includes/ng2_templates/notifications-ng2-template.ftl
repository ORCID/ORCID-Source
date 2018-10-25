<script type="text/ng-template" id="notifications-ng2-template">
    <div class="col-md-9 col-sm-12 col-xs-12">        
        <div class="notification-top-bar">
            <ul class="inline-list pull-right">
                <li *ngIf="bulkArchiveMap?.length > 0 && selectionActive" >
                    <button class="btn btn-primary" (click)="bulkArchive()"><i class="glyphicon glyphicon-download-alt"></i> ${springMacroRequestContext.getMessage("notifications.archive_selected")}</button>                   
                </li>
                <li>&nbsp;</li>
                <li>
                    <button class="btn btn-primary" (click)="toggleArchived()">
                        <span *ngIf="!notificationsSrvc.showArchived" >${springMacroRequestContext.getMessage("notifications.show_archived")}</span>
                        <span *ngIf="notificationsSrvc.showArchived" >${springMacroRequestContext.getMessage("notifications.hide_archived")}</span>
                    </button>                   
                </li>
            </ul>   
        </div>
        <div *ngIf="loading == true" class="text-center" id="notificationsSpinner">
            <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
        </div>
        <div  *ngIf="loading == false && notificationsSrvc?.notifications?.length == 0  && !areMore">${springMacroRequestContext.getMessage("notifications.none")}</div>
        <div *ngIf="loading == false && notificationsSrvc?.notifications?.length > 0">            
            <table class="table table-responsive table-condensed notifications">
                <thead>                 
                    <tr>                        
                        <th>${springMacroRequestContext.getMessage("notifications.from")}</th>
                        <th>${springMacroRequestContext.getMessage("notifications.subject")}</th>
                        <th>${springMacroRequestContext.getMessage("notifications.date")}</th>
                        <th></th>       
                        <th class="centered">                           
                            <input type="checkbox" value="" (click)="swapbulkChangeAll()" [(ngModel)]="bulkChecked">
                        </th>
                    </tr>
                </thead>
                <tbody>
                    <ng-container *ngFor="let notification of notificationsSrvc?.notifications"> 
                        <tr [ngClass]="{unread: !notification.readDate, archived: notification.archivedDate}">                       
                            <td (click)="toggleDisplayBody(notification.putCode)">
                                <i class="glyphicon-chevron-down glyphicon x0" [ngClass]="{'glyphicon-chevron-right':!displayBody[notification.putCode]}"></i>
                                <span *ngIf="notification.overwrittenSourceName">{{notification.overwrittenSourceName}}</span>
                                <span *ngIf="!notification.overwrittenSourceName && notification.source" >{{notification.source.sourceName.content}}</span><span *ngIf="!notification.overwrittenSourceName || !notification.source" >ORCID</span>
                            </td>
                            <td (click)="toggleDisplayBody(notification.putCode)"><span >{{notification.subject}}</span></td>
                            <td (click)="toggleDisplayBody(notification.putCode)"><span >{{notification.createdDate | date:'yyyy-MM-dd'}}</span></td>
                            <td class="centered">
                                <span *ngIf="!notification.archivedDate"><a (click)="archive(notification.putCode)" class="glyphicon glyphicon-download-alt dark-grey" title="${springMacroRequestContext.getMessage("notifications.archive")}"></a></span>
                            </td>
                            <td class="centered">
                                <input type="checkbox" class="centered archive-checkbox" [(ngModel)]="bulkArchiveMap[notification.putCode]" *ngIf="!notification.archivedDate" (ngModelChange)="checkSelection()">
                            </td>
                        </tr>
                        <tr *ngIf="displayBody[notification.putCode]">
                            <td colspan="5">
                                <iframe id="{{notification.putCode}}" [src]="'<@orcid.rootPath '/inbox'/>/' + notification.notificationType + '/' + notification.putCode + '/notification.html' | safeUrl" class="notification-iframe" frameborder="0" width="100%" scrolling="no"></iframe>
                            </td>
                        </tr>
                    </ng-container>                 
                </tbody>
            </table>
        </div>
        <div *ngIf="!(loading == false && notificationsSrvc?.notifications?.length > 0)">
            <br/><br/>
        </div>   
        <div >
            <button *ngIf="areMore && loadingMore == false" (click)="showMore()" class="btn btn-primary" type="submit" id="show-more-button">${springMacroRequestContext.getMessage("notifications.show_more")}</button>
        </div>
        <div  *ngIf="loadingMore == true" id="moreNotificationsSpinner">
            <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
        </div>
    </div>
</script>