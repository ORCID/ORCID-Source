declare var iframeResize: any;
declare var $q: any;

import { Injectable } 
    from '@angular/core';

import { HttpClient } 
     from '@angular/common/http';


import { Headers, Http, RequestOptions, Response } 
    from '@angular/http';

import { Observable } 
    from 'rxjs/Observable';

import 'rxjs/Rx';

@Injectable()
export class NotificationsService {
    private headers: Headers;

    defaultMaxResults: number;
    loading: boolean;
    loadingMore: boolean;
    firstResult: number;
    maxResults: any;
    areMoreFlag: boolean;
    notifications: any;
    displayBody: any;
    unreadCount: number;
    showArchived: boolean;
    bulkChecked: boolean;
    bulkArchiveMap: any;
    selectionActive: boolean;
    notificationAlerts: any;

    constructor( private http: HttpClient ){
        this.headers = new Headers(
            { 
                'Content-Type': 'application/json' 
            }
        );

        this.loading = true;
        this.loadingMore = false;
        this.firstResult = 0;
        this.maxResults = this.defaultMaxResults = 10;
        this.areMoreFlag = false;
        this.notifications = [];
        this.displayBody = {};
        this.unreadCount = 0;
        this.showArchived = false;
        this.bulkChecked = false;
        this.bulkArchiveMap = [];
        this.selectionActive = false;
        this.notificationAlerts = [];
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

    getUnreadCount(): any {
        return this.unreadCount;
    }

    showMore(): void {
        this.loadingMore = true;
        this.firstResult += this.maxResults;
        this.getNotifications();
    }

    areMore(): boolean {
        return this.areMoreFlag;
    }

    toggleArchived(): void {
        this.showArchived = !this.showArchived;
        this.reloadNotifications();
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

    getNotifications(): any {
        var url = getBaseUri() + '/inbox/notifications.json?firstResult=' + this.firstResult + '&maxResults=' + this.maxResults;             
        if(this.showArchived){
            url += "&includeArchived=true";
        }
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
    }

    getNotificationAlerts(): any{
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
    }

    retrieveUnreadCount(): any {
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
    }

    flagAsRead(notificationId): any {
            
        console.log(notificationId);
        
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
    }

    archive(notificationId): any {         
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
    }

    suppressAlert(notificationId): any {         
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
    }

    bulkArchive(): any {            
        var promises = [];
        var tmpNotifications = this.notifications;
        
        function archive(notificationId){                
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
        }
        
        for (let putCode in this.bulkArchiveMap) {
            if(this.bulkArchiveMap[putCode]) {
                promises.push(archive(putCode));            
            }
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