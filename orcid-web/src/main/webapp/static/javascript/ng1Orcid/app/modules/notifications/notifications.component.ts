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
    private subscription: Subscription;
   
    areMore: boolean;
    bulkArchiveMap: any;
    bulkChecked: any;
    displayBody: any;
    loading: boolean;
    loadingMore: boolean;
    selectionActive: boolean;

    constructor(
        private notificationsService: NotificationsService
    ) {

        this.areMore = false;   
        this.bulkArchiveMap = [];
        this.bulkChecked = false;
        this.displayBody = {};
        this.loading = true;
        this.loadingMore = false;  
        this.selectionActive = false;
    }

    archive( notificationId ): any {
        this.notificationsService.archive(notificationId)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        ).subscribe(
            data => {
                var updated = data;
                for(var i = 0;  i < this.notificationsService.notifications.length; i++){
                    var existing = this.notificationsService.notifications[i];
                    if(existing.putCode === updated['putCode']){
                        this.notificationsService.notifications.splice(i, 1);
                        if(this.notificationsService.firstResult > 0){
                            this.notificationsService.firstResult--;
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
        for (let i = 0; i < this.notificationsService.notifications.length; i++){
            if (this.notificationsService.notifications[i].archivedDate == null) {
                totalNotifications++;            
            }
            
        }              
        
        totalNotifications == count ? this.bulkChecked = true : this.bulkChecked = false;
    }

    flagAsRead( notificationId ): any {       
        this.notificationsService.flagAsRead(notificationId)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        ).subscribe(
            data => {
                var updated = data;
                for(var i = 0;  i < this.notificationsService.notifications.length; i++){
                    var existing = this.notificationsService.notifications[i];
                    if(existing.putCode === updated['putCode']){
                        existing.readDate = updated['readDate'];
                    }
                }
                this.retrieveUnreadCount();                        
            }
        );
    }

    getNotifications(): void {
        this.notificationsService.getNotifications()
        .pipe(    
        takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(data.length === 0 || data.length < this.notificationsService.maxResults){
                    this.areMore = false;
                }
                else{
                    this.areMore = true;
                }
                for(var i = 0; i < data.length; i++){                       
                    this.notificationsService.notifications.push( data[i] );
                }
                this.loading = false;
                this.loadingMore = false;
                this.resizeIframes();
                this.retrieveUnreadCount();                                      
            }
        );
    }

    reloadNotifications(): void {
        this.loading = true;
        this.notificationsService.notifications.length = 0;
        this.notificationsService.firstResult = 0;
        this.notificationsService.maxResults = this.notificationsService.defaultMaxResults;
        this.getNotifications();            
    }

    resizeIframes(): void {
        let activeViews = this.displayBody;
        for (let key in activeViews){
            iframeResize(key);              
        }
    }

    retrieveUnreadCount(): any {
        this.notificationsService.retrieveUnreadCount()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.notificationsService.unreadCount = data;                                            
            }
        );
    }

    showMore(): void {
        this.loadingMore = true;
        this.notificationsService.firstResult += this.notificationsService.maxResults;
        this.getNotifications();
    }

    swapbulkChangeAll(): void {          
        this.bulkChecked = !this.bulkChecked;
        if(this.bulkChecked == false) {
            this.bulkArchiveMap.length = 0;
        } else {
            for (let idx in this.notificationsService.notifications) {
                this.bulkArchiveMap[this.notificationsService.notifications[idx].putCode] = this.bulkChecked;
            }
            this.selectionActive = true;
        }
    }

    toggleArchived(): void {
        this.notificationsService.showArchived = !this.notificationsService.showArchived;
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
        this.subscription = this.notificationsService.notifyObservable$.subscribe(
            (res) => {                
                if (res.action == 'archive' && res.putCode) {
                    this.archive(res.putCode);
                }                
            }
        );
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getNotifications();
    }; 
}