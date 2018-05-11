declare var iframeResize: any;
declare var $q: any;

import { Injectable } 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { Observable } 
    from 'rxjs/Observable';

import { Subject }
    from 'rxjs/Subject';

import 'rxjs/Rx';

@Injectable()
export class NotificationsService {
    private headers: HttpHeaders;
    private notify = new Subject<any>();

    public retrieveCountCalled: boolean;

    
    notifyObservable$ = this.notify.asObservable();

    areMoreFlag: boolean;
    bulkChecked: boolean;
    bulkArchiveMap: any;
    defaultMaxResults: number;
    displayBody: any;
    firstResult: number;
    loading: boolean;
    loadingMore: boolean;
    maxResults: any;
    notificationAlerts: any;
    notifications: any;
    
    selectionActive: boolean;
    showArchived: boolean;
    unreadCount: any;

    constructor( private http: HttpClient ){
        this.headers = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector("meta[name='_csrf']").getAttribute("content")
            }
        );

        this.areMoreFlag = false;
        this.bulkArchiveMap = [];
        this.bulkChecked = false;
        this.displayBody = {};
        this.firstResult = 0;
        this.loading = true;
        this.loadingMore = false;
        this.maxResults = this.defaultMaxResults = 10;
        this.retrieveCountCalled = false;
        this.notificationAlerts = [];
        this.notifications = [];
        this.retrieveCountCalled = false;
        this.selectionActive = false;
        this.showArchived = false;
        this.unreadCount = 0;
    }

    areMore(): boolean {
        return this.areMoreFlag;
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
        for (let i = 0; i < this.notifications.length; i++){
            if (this.notifications[i].archivedDate == null) {
                totalNotifications++;            
            }
            
        }              
        
        totalNotifications == count ? this.bulkChecked = true : this.bulkChecked = false;
    }

    getUnreadCount(): any {
        return this.unreadCount;
    }

    reloadNotifications(): void {
        this.loading = true;
        this.notifications.length = 0;
        this.firstResult = 0;
        this.maxResults = this.defaultMaxResults;
        this.getNotifications();            
    }

    resizeIframes(): void {
        let activeViews = this.displayBody;
        for (let key in activeViews){
            iframeResize(key);              
        }
    }

    showMore(): void {
        this.loadingMore = true;
        this.firstResult += this.maxResults;
        this.getNotifications();
    }

    swapbulkChangeAll(): void {          
        this.bulkChecked = !this.bulkChecked;
        if(this.bulkChecked == false) {
            this.bulkArchiveMap.length = 0;
        }
        else {
            for (let idx in this.notifications) {
                this.bulkArchiveMap[this.notifications[idx].putCode] = this.bulkChecked;
            }
            this.selectionActive = true;
        }
    }

    toggleArchived(): void {
        this.showArchived = !this.showArchived;
        this.reloadNotifications();
    }

    

    getNotifications(): any {
        var url = getBaseUri() + '/inbox/notifications.json?firstResult=' + this.firstResult + '&maxResults=' + this.maxResults;             
        if(this.showArchived){
            url += "&includeArchived=true";
        }

        return this.http.get(
            url
        )
        .do(
            (data: any) => {
                if(data.length === 0 || data.length < this.maxResults){
                    this.areMoreFlag = false;
                }
                else{
                    this.areMoreFlag = true;
                }
                for(var i = 0; i < data.length; i++){                       
                    this.notifications.push( data[i] );
                }
                this.loading = false;
                this.loadingMore = false;
                this.resizeIframes();
                this.retrieveUnreadCount();                                             
            }
        )
        .share();

        /*
        $.ajax({
            url: url,
            dataType: 'json',
            success: function(data) {
                if(data.length === 0 || data.length < this.maxResults){
                    this.areMoreFlag = false;
                }
                else{
                    this.areMoreFlag = true;
                }
                for(var i = 0; i < data.length; i++){                       
                    this.notifications.push(data[i]);
                }
                this.loading = false;
                this.loadingMore = false;
                this.resizeIframes();
                this.retrieveUnreadCount();
            }
        }).fail(function(e) {
            this.loading = false;
            this.loadingMore = false;
            // something bad is happening!
            console.log("error with getting notifications");
        });
        */
    }

    getNotificationAlerts(): any{
        return this.http.get(
            getBaseUri() + '/inbox/notification-alerts.json'
        )
        .do(
            (data) => {
                this.notificationAlerts = data;
                this.retrieveUnreadCount();                                              
            }
        )
        .share();
        /*
        $.ajax({
            url: getBaseUri() + '/inbox/notification-alerts.json',
            type: 'POST',
            dataType: 'json',
            success: function(data) {
                this.notificationAlerts = data;
                this.retrieveUnreadCount();
            }
        }).fail(function(e) {
            // something bad is happening!
            console.log("getNotificationsAlerts error in notificationsSrvc");
        });
        */
    }

    retrieveUnreadCount(): any {
        this.retrieveCountCalled = true;

        return this.http.get(
            getBaseUri() + '/inbox/unreadCount.json'
        )
        .do(
            (data) => {
                this.unreadCount = data;                                             
            }
        )
        .share();
        /*
        $.ajax({
            url: getBaseUri() + '/inbox/unreadCount.json',
            dataType: 'json',
            success: function(data) {
                this.unreadCount = data; 
            }
        }).fail(function(e) {
            // something bad is happening!
            console.log("error with getting count of unread notifications");
        });
        */
    }

    flagAsRead( obj ): any {
            
        let encoded_data = JSON.stringify( obj );
        
        return this.http.post( 
            getBaseUri() + '/inbox/' + obj + '/read.json', 
            encoded_data, 
            { headers: this.headers }
        )
        .do(
            (data) => {
                var updated = data;
                for(var i = 0;  i < this.notifications.length; i++){
                    var existing = this.notifications[i];
                    if(existing.putCode === updated['putCode']){
                        existing.readDate = updated['readDate'];
                    }
                }
                this.retrieveUnreadCount();                        
            }
        )
        .share();
        /*
        $.ajax({
            url: getBaseUri() + '/inbox/' + notificationId + '/read.json',
            type: 'POST',
            dataType: 'json',
            success: function(data) {
                var updated = data;
                for(var i = 0;  i < this.notifications.length; i++){
                    var existing = this.notifications[i];
                    if(existing.putCode === updated.putCode){
                        existing.readDate = updated.readDate;
                    }
                }
                this.retrieveUnreadCount();
                //$rootScope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("error flagging notification as read");
        });
        */
    }

    archive(obj): any {
        let encoded_data = JSON.stringify( obj );
        
        return this.http.post( 
            getBaseUri() + '/inbox/' + obj + '/archive.json', 
            encoded_data, 
            { headers: this.headers }
        )
        .do(
            (data) => {
                var updated = data;
                for(var i = 0;  i < this.notifications.length; i++){
                    var existing = this.notifications[i];
                    if(existing.putCode === updated['putCode']){
                        this.notifications.splice(i, 1);
                        if(this.firstResult > 0){
                            this.firstResult--;
                        }
                        break;
                    }
                }
                this.retrieveUnreadCount();                       
            }
        )
        .share();

        /*
        $.ajax({
            url: getBaseUri() + '/inbox/' + notificationId + '/archive.json',
            type: 'POST',
            dataType: 'json',
            success: function(data) {
                var updated = data;
                for(var i = 0;  i < this.notifications.length; i++){
                    var existing = this.notifications[i];
                    if(existing.putCode === updated.putCode){
                        this.notifications.splice(i, 1);
                        if(this.firstResult > 0){
                            this.firstResult--;
                        }
                        break;
                    }
                }
                this.retrieveUnreadCount();
                //$rootScope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("error flagging notification as archived");
        });
        */
    }

    suppressAlert(notificationId): any {      
        return this.http.get(
            getBaseUri() + '/inbox/' + notificationId + '/suppressAlert.json',
        )
        .do(
            (data) => {
                for(var i = 0;  i < this.notifications.length; i++){
                    var existing = this.notifications[i];
                    if(existing.putCode === notificationId){
                        this.notifications.splice(i, 1);
                        if(this.firstResult > 0){
                            this.firstResult--;
                        }
                        break;
                    }
                }                                            
            }
        )
        .share();

        /*
        $.ajax({
            url: getBaseUri() + '/inbox/' + notificationId + '/suppressAlert.json',
            type: 'POST',
            dataType: 'json',
            success: function(data) {
                for(var i = 0;  i < this.notifications.length; i++){
                    var existing = this.notifications[i];
                    if(existing.putCode === notificationId){
                        this.notifications.splice(i, 1);
                        if(this.firstResult > 0){
                            this.firstResult--;
                        }
                        break;
                    }
                }
                //$rootScope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("error flagging notification alert as suppressed");
        });
        */
    }

    bulkArchive(): any {            
        var promises = [];
        var tmpNotifications = this.notifications;
        
        for (let putCode in this.bulkArchiveMap) {
            if(this.bulkArchiveMap[putCode]) {
                promises.push(archive(putCode));            
            }
        }
        
        function archive(notificationId){  
            return this.http.get(
                getBaseUri() + '/inbox/' + notificationId + '/archive.json'
            )
            .do(
                (data) => {
                                                             
                }
            )
            .share();

            /*
            var defer = $q.defer(notificationId);                
            $.ajax({
                url: getBaseUri() + '/inbox/' + notificationId + '/archive.json',
                type: 'POST',
                dataType: 'json',
                success: function(data) {
                    defer.resolve(notificationId);
                }
            }).fail(function() {
                // something bad is happening!
                console.log("error flagging notification as archived");
            });                
            return defer.promise;
            */
        }
        
        
        /*
        $q.all(promises).then(function(){
            this.bulkArchiveMap.length = 0;
            this.bulkChecked = false;
            this.reloadNotifications();
        });
        */
        
    }
}