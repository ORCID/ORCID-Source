//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';

import { takeUntil } 
    from 'rxjs/operators';

import { NotificationsService } 
    from '../../shared/notifications.service.ts'; 


@Component({
    selector: 'notification-alerts-ng2',
    template:  scriptTmpl("notification-alerts-ng2-template")
})
export class NotificationAlertsComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    
    constructor(
        private notificationsSrvc: NotificationsService
    ) {
    }

    archive( notificationId ): any {
        this.notificationsSrvc.archive(notificationId)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        ).subscribe(
            data => {
                var updated = data;
                for(var i = 0;  i < this.notificationsSrvc.notificationAlerts.length; i++){
                    var existing = this.notificationsSrvc.notificationAlerts[i];
                    if(existing.putCode === updated['putCode']){
                        this.notificationsSrvc.notificationAlerts.splice(i, 1);
                        if(this.notificationsSrvc.firstResult > 0){
                            this.notificationsSrvc.firstResult--;
                        }
                        break;
                    }
                }
                this.retrieveUnreadCount();                       
            }
        );
    }

     getNotificationAlerts(): void {
        this.notificationsSrvc.getNotificationAlerts()
        .pipe(    
        takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.notificationsSrvc.notificationAlerts = data;
                this.retrieveUnreadCount();
                for(var i = 0;  i < this.notificationsSrvc.notificationAlerts.length; i++) {
                    if (this.notificationsSrvc.notificationAlerts[i].authorizationUrl){
                        var encodedUrl = encodeURIComponent(this.notificationsSrvc.notificationAlerts[i].authorizationUrl.uri);
                        this.notificationsSrvc.notificationAlerts[i].authorizationUrl.uri = encodedUrl;
                    }
                }

            }
        );
    }

    retrieveUnreadCount(): any {
        this.notificationsSrvc.retrieveUnreadCount()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.notificationsSrvc.unreadCount = data;                                            
            }
        );
    }

    suppressAlert(notificationId): any {      
        this.notificationsSrvc.suppressAlert(notificationId)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                for(var i = 0;  i < this.notificationsSrvc.notificationAlerts.length; i++){
                    var existing = this.notificationsSrvc.notificationAlerts[i];
                    if(existing.putCode === notificationId){
                        this.notificationsSrvc.notificationAlerts.splice(i, 1);
                        if(this.notificationsSrvc.firstResult > 0){
                            this.notificationsSrvc.firstResult--;
                        }
                        break;
                    }
                }                                            
            }
        );
    }

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getNotificationAlerts();
    }; 
    
    getBaseUri(): String {
        return getBaseUri();
    };
}
