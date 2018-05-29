//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

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

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.notificationsSrvc.getNotificationAlerts();
    }; 
}
