declare var iframeResize: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit, TemplateRef } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil, tap } 
    from 'rxjs/operators';

import { NotificationsService } 
    from '../../shared/notifications.service.ts'; 


@Component({
    selector: 'notifications-ng2',
    template:  scriptTmpl("notifications-ng2-template")
})
export class NotificationsComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
   
    areMore: boolean;
    bulkArchiveMap: any;
    bulkChecked: any;
    displayBody: any;
    loading: boolean;
    loadingMore: boolean;
    notifications: any;
    selectionActive: boolean;

    constructor(
        private notificationsSrvc: NotificationsService
    ) {

        this.areMore = false;   
        this.bulkArchiveMap = [];
        this.bulkChecked = false;
        this.displayBody = {};
        this.loading = true;
        this.loadingMore = false;
        this.notifications = this.notificationsSrvc.notifications;   
        this.selectionActive = false;
    }

    archive( notificationId ): any {
        this.notificationsSrvc.archive(notificationId)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        ).subscribe(
            data => {
                var updated = data;
                for(var i = 0;  i < this.notificationsSrvc.notifications.length; i++){
                    var existing = this.notificationsSrvc.notifications[i];
                    if(existing.putCode === updated['putCode']){
                        this.notificationsSrvc.notifications.splice(i, 1);
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

    bulkArchive(): any {            
        for (let putCode in this.bulkArchiveMap) {
            if(this.bulkArchiveMap[putCode]) {
                this.archive(putCode);           
            }
        }
    }

    checkSelection(): void{
        let count = 0;
        let totalNotifications = 0;            
        this.selectionActive = false;
        for (let putCode in this.bulkArchiveMap){                
            if(this.bulkArchiveMap[putCode] == true){
                this.selectionActive = true;
                count++;
            }
        }                      
        for (let i = 0; i < this.notificationsSrvc.notifications.length; i++){
            if (this.notificationsSrvc.notifications[i].archivedDate == null) {
                totalNotifications++;            
            }
            
        }              
        
        totalNotifications == count ? this.bulkChecked = true : this.bulkChecked = false;
    }

    flagAsRead( notificationId ): any {       
        this.notificationsSrvc.flagAsRead(notificationId)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        ).subscribe(
            data => {
                var updated = data;
                for(var i = 0;  i < this.notificationsSrvc.notifications.length; i++){
                    var existing = this.notificationsSrvc.notifications[i];
                    if(existing.putCode === updated['putCode']){
                        existing.readDate = updated['readDate'];
                    }
                }
                this.retrieveUnreadCount();                        
            }
        );
    }

    getNotifications(): void {
        this.notificationsSrvc.getNotifications()
        .pipe(    
        takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(data.length === 0 || data.length < this.notificationsSrvc.maxResults){
                    this.areMore = false;
                }
                else{
                    this.areMore = true;
                }
                for(var i = 0; i < data.length; i++){                       
                    this.notificationsSrvc.notifications.push( data[i] );
                }
                this.loading = false;
                this.loadingMore = false;
                this.resizeIframes();
                this.notificationsSrvc.retrieveUnreadCount();                                             
            }
        );
    }

    reloadNotifications(): void {
        this.loading = true;
        this.notificationsSrvc.notifications.length = 0;
        this.notificationsSrvc.firstResult = 0;
        this.notificationsSrvc.maxResults = this.notificationsSrvc.defaultMaxResults;
        this.getNotifications();            
    }

    resizeIframes(): void {
        let activeViews = this.displayBody;
        for (let key in activeViews){
            iframeResize(key);              
        }
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

    showMore(): void {
        this.loadingMore = true;
        this.notificationsSrvc.firstResult += this.notificationsSrvc.maxResults;
        this.getNotifications();
    }

    swapbulkChangeAll(): void {          
        this.bulkChecked = !this.bulkChecked;
        if(this.bulkChecked == false) {
            this.bulkArchiveMap.length = 0;
        } else {
            for (let idx in this.notificationsSrvc.notifications) {
                this.bulkArchiveMap[this.notificationsSrvc.notifications[idx].putCode] = this.bulkChecked;
            }
            this.selectionActive = true;
        }
    }

    toggleArchived(): void {
        this.notificationsSrvc.showArchived = !this.notificationsSrvc.showArchived;
        this.reloadNotifications();
    }

    toggleDisplayBody(notificationId): void {
        this.displayBody[notificationId] = !this.displayBody[notificationId];        
        this.flagAsRead(notificationId);
        iframeResize(notificationId);
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getNotifications();
    }; 
}