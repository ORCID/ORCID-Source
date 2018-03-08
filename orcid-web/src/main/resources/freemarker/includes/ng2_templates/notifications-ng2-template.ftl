<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->

<script type="text/ng-template" id="notifications-ng2-template">
    <div class="col-md-9 col-sm-12 col-xs-12">        
        <div class="notification-top-bar">
            <ul class="inline-list pull-right">
                <li *ngIf="notificationsSrvc?.bulkArchiveMap?.length > 0 && notificationsSrvc?.selectionActive" >
                    <button class="btn btn-primary" (click)="notificationsSrvc.bulkArchive()" ><i class="glyphicon glyphicon-download-alt"></i> ${springMacroRequestContext.getMessage("notifications.archive_selected")}</button>                   
                </li>
                <li>&nbsp;</li>
                <li>
                    <button class="btn btn-primary" (click)="notificationsSrvc.toggleArchived()">
                        <span *ngIf="!notificationsSrvc.showArchived" >${springMacroRequestContext.getMessage("notifications.show_archived")}</span>
                        <span *ngIf="notificationsSrvc.showArchived" >${springMacroRequestContext.getMessage("notifications.hide_archived")}
                    </button>                   
                </li>
            </ul>
            
        </div>
        <div *ngIf="notificationsSrvc?.loading == true" class="text-center" id="notificationsSpinner">
            <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
        </div>
        <div  *ngIf="notificationsSrvc?.loading == false && notifications?.length == 0  &&!areMore()">${springMacroRequestContext.getMessage("notifications.none")}</div>
        <div  *ngIf="notificationsSrvc?.loading == false && notifications?.length &gt; 0">            
            <table class="table table-responsive table-condensed notifications">
                <thead>                 
                    <tr>                        
                        <th>${springMacroRequestContext.getMessage("notifications.from")}</th>
                        <th>${springMacroRequestContext.getMessage("notifications.subject")}</th>
                        <th>${springMacroRequestContext.getMessage("notifications.date")}</th>
                        <th></th>       
                        <th class="centered">                           
                            <input type="checkbox" value="" (click)="notificationsSrvc.swapbulkChangeAll()" [(ngModel)]="bulkChecked">
                        </th>
                    </tr>
                </thead>
                <tbody>
                    <tr *ngFor="let notification of notifications" [ngClass]="{unread: !notification.readDate, archived: notification.archivedDate}">                       
                        <td (click)="toggleDisplayBody(notification.putCode)">
                            <i class="glyphicon-chevron-down glyphicon x0" [ngClass]="{'glyphicon-chevron-right':!displayBody[notification.putCode]}"></i>
                            <span *ngIf="notification.overwrittenSourceName">{{notification.overwrittenSourceName}}</span>
                            <span *ngIf="!notification.overwrittenSourceName && notification.source" >{{notification.source.sourceName.content}}</span><span ng-hide="notification.overwrittenSourceName || notification.source" >ORCID</span>
                        </td>
                        <td (click)="toggleDisplayBody(notification.putCode)"><span >{{notification.subject}}</span></td>
                        <td (click)="toggleDisplayBody(notification.putCode)"><span >{{notification.createdDate|humanDate}}</span></td>
                        <td class="centered">
                            <span *ngIf="!notification.archivedDate"><a href="" (click)="archive(notification.putCode)" class="glyphicon glyphicon-download-alt dark-grey" title="${springMacroRequestContext.getMessage("notifications.archive")}"></a></span>
                        </td>
                        <td class="centered">
                            <input type="checkbox" class="centered archive-checkbox" [(ngModel)]="notificationsSrvc.bulkArchiveMap[notification.putCode]" *ngIf="!notification.archivedDate" (ngModelChange)="notificationsSrvc.checkSelection()">
                        </td>
                    </tr>
                    <tr *ngFor *ngIf="displayBody[notification.putCode]" (click)="return false;">
                        <td colspan="5">
                            <iframe id="{{notification.putCode}}" [src]="{{ '<@orcid.rootPath '/inbox'/>/' + notification.notificationType + '/' + notification.putCode + '/notification.html'}}" class="notification-iframe" frameborder="0" width="100%" scrolling="no"></iframe>
                        </td>
                    </tr>                   
                </tbody>

            </table>
        </div>
        <div  *ngIf="!(notificationsSrvc?.loading == false && notifications?.length > 0)">
            <br/><br/>
        </div>   
        <div >
            <button *ngIf="areMore() && notificationsSrvc?.loadingMore == false" (click)="showMore()" class="btn" type="submit" id="show-more-button">${springMacroRequestContext.getMessage("notifications.show_more")}</button>
        </div>
        <div  *ngIf="notificationsSrvc?.loadingMore == true" id="moreNotificationsSpinner">
            <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
        </div>
    </div>
</script>