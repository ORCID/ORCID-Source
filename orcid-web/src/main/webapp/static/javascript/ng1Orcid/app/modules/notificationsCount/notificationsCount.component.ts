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
    selector: 'notifications-count-ng2',
    template:  scriptTmpl("notifications-count-ng2-template")
})
export class NotificationsCountComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
   
    getUnreadCount: any;

    constructor(
        private notificationsSrvc: NotificationsService
    ) {

        this.getUnreadCount = this.notificationsSrvc.getUnreadCount;
    
    }

    isCurrentPage(path): any {
        return window.location.href.startsWith(orcidVar.baseUri + '/' + path);
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

        if(!(this.isCurrentPage('my-orcid') || this.isCurrentPage('inbox'))){
            this.notificationsSrvc.retrieveUnreadCount();
        }
    }; 
}