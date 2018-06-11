declare var iframeResize: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import 'rxjs/add/operator/takeUntil';

import { NotificationsService } 
    from '../../shared/notifications.service.ts'; 


@Component({
    selector: 'notifications-ng2',
    template:  scriptTmpl("notifications-ng2-template")
})
export class NotificationsComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
   
    displayBody: any;
    archive: any;
    areMore: any;
    bulkArchiveMap: any;
    bulkChecked: any;
    getNotifications: any;
    notifications: any;
    reloadNotifications: any;
    showMore: any;

    constructor(
        private notificationsSrvc: NotificationsService
    ) {

        this.notificationsSrvc.displayBody = {};    
        this.archive = this.notificationsSrvc.archive;
        this.areMore = this.notificationsSrvc.areMore;
        this.bulkArchiveMap = this.notificationsSrvc.bulkArchiveMap;
        this.bulkChecked = this.notificationsSrvc.bulkChecked;
        this.displayBody = {};
        this.getNotifications = this.notificationsSrvc.getNotifications;
        this.notifications = this.notificationsSrvc.notifications;
        this.reloadNotifications = this.notificationsSrvc.reloadNotifications;
        this.showMore = this.notificationsSrvc.showMore;
    
    }

    toggleDisplayBody(notificationId): void {
        this.displayBody[notificationId] = !this.displayBody[notificationId];        
        this.notificationsSrvc.displayBody[notificationId] = this.displayBody[notificationId]; 
        this.notificationsSrvc.flagAsRead(notificationId);
        iframeResize(notificationId);
    };

    /* pending ***
    $scope.$watch(function () { 
        return $scope.notificationsSrvc.bulkChecked }, 
        function (newVal, oldVal) {
            if (typeof newVal !== 'undefined') {
                $scope.bulkChecked = $scope.notificationsSrvc.bulkChecked;
            }
        }
    );
    */

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.notificationsSrvc.getNotifications();
    }; 
}