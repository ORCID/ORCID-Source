angular.module('orcidApp').factory("notificationsSrvc", ['$rootScope', '$q', function ($rootScope, $q) {
    var defaultMaxResults = 10;
    var serv = {
        loading: true,
        loadingMore: false,
        firstResult: 0,
        maxResults: defaultMaxResults,
        areMoreFlag: false,
        notifications: [],
        displayBody: {},
        unreadCount: 0,
        showArchived: false,
        bulkChecked: false,
        bulkArchiveMap: [],
        selectionActive: false,
        notificationAlerts: [],
        getNotifications: function() {
            var url = getBaseUri() + '/inbox/notifications.json?firstResult=' + serv.firstResult + '&maxResults=' + serv.maxResults;             
            if(serv.showArchived){
                url += "&includeArchived=true";
            }
            $.ajax({
                url: url,
                dataType: 'json',
                success: function(data) {
                    if(data.length === 0 || data.length < serv.maxResults){
                        serv.areMoreFlag = false;
                    }
                    else{
                        serv.areMoreFlag = true;
                    }
                    for(var i = 0; i < data.length; i++){                       
                        serv.notifications.push(data[i]);
                    }
                    serv.loading = false;
                    serv.loadingMore = false;
                    $rootScope.$apply();
                    serv.resizeIframes();
                    serv.retrieveUnreadCount();
                }
            }).fail(function(e) {
                serv.loading = false;
                serv.loadingMore = false;
                // something bad is happening!
                console.log("error with getting notifications");
                logAjaxError(e);
            });
        },
        getNotificationAlerts: function(){
            $.ajax({
                url: getBaseUri() + '/inbox/notification-alerts.json',
                type: 'POST',
                dataType: 'json',
                success: function(data) {
                    serv.notificationAlerts = data;
                    serv.retrieveUnreadCount();
                }
            }).fail(function(e) {
                // something bad is happening!
                console.log("getNotificationsAlerts error in notificationsSrvc");
                logAjaxError(e);
            });
        },
        reloadNotifications: function() {
            serv.loading = true;
            serv.notifications.length = 0;
            serv.firstResult = 0;
            serv.maxResults = defaultMaxResults;
            serv.getNotifications();            
        },
        retrieveUnreadCount: function() {
            $.ajax({
                url: getBaseUri() + '/inbox/unreadCount.json',
                dataType: 'json',
                success: function(data) {
                    serv.unreadCount = data;                   
                    $rootScope.$apply();
                }
            }).fail(function(e) {
                // something bad is happening!
                console.log("error with getting count of unread notifications");
                logAjaxError(e);
            });
        },
        resizeIframes: function(){
            var activeViews = serv.displayBody;
            for (key in activeViews){
                iframeResize(key);              
            }
        },
        getUnreadCount: function() {
            return serv.unreadCount;
        },
        showMore: function() {
            serv.loadingMore = true;
            serv.firstResult += serv.maxResults;
            serv.getNotifications();
        },
        areMore: function() {
            return serv.areMoreFlag;
        },
        flagAsRead: function(notificationId) {
            
            console.log(notificationId);
            
            $.ajax({
                url: getBaseUri() + '/inbox/' + notificationId + '/read.json',
                type: 'POST',
                dataType: 'json',
                success: function(data) {
                    var updated = data;
                    for(var i = 0;  i < serv.notifications.length; i++){
                        var existing = serv.notifications[i];
                        if(existing.putCode === updated.putCode){
                            existing.readDate = updated.readDate;
                        }
                    }
                    serv.retrieveUnreadCount();
                    $rootScope.$apply();
                }
            }).fail(function() {
                // something bad is happening!
                console.log("error flagging notification as read");
            });
        },
        archive: function(notificationId) {         
            $.ajax({
                url: getBaseUri() + '/inbox/' + notificationId + '/archive.json',
                type: 'POST',
                dataType: 'json',
                success: function(data) {
                    var updated = data;
                    for(var i = 0;  i < serv.notifications.length; i++){
                        var existing = serv.notifications[i];
                        if(existing.putCode === updated.putCode){
                            serv.notifications.splice(i, 1);
                            if(serv.firstResult > 0){
                                serv.firstResult--;
                            }
                            break;
                        }
                    }
                    serv.retrieveUnreadCount();
                    $rootScope.$apply();
                }
            }).fail(function() {
                // something bad is happening!
                console.log("error flagging notification as archived");
            });
        },
        suppressAlert: function(notificationId) {         
            $.ajax({
                url: getBaseUri() + '/inbox/' + notificationId + '/suppressAlert.json',
                type: 'POST',
                dataType: 'json',
                success: function(data) {
                    for(var i = 0;  i < serv.notifications.length; i++){
                        var existing = serv.notifications[i];
                        if(existing.putCode === notificationId){
                            serv.notifications.splice(i, 1);
                            if(serv.firstResult > 0){
                                serv.firstResult--;
                            }
                            break;
                        }
                    }
                    $rootScope.$apply();
                }
            }).fail(function() {
                // something bad is happening!
                console.log("error flagging notification alert as suppressed");
            });
        },
        toggleArchived: function(){
            serv.showArchived = !serv.showArchived;
            serv.reloadNotifications();
        },
        swapbulkChangeAll: function(){          
            serv.bulkChecked = !serv.bulkChecked;
            if(serv.bulkChecked == false)
                serv.bulkArchiveMap.length = 0;
            else
                for (var idx in serv.notifications)
                    serv.bulkArchiveMap[serv.notifications[idx].putCode] = serv.bulkChecked;
                serv.selectionActive = true;
            
            
        },
        bulkArchive: function(){            
            var promises = [];
            var tmpNotifications = serv.notifications;
            
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
            
            for (putCode in serv.bulkArchiveMap)
                if(serv.bulkArchiveMap[putCode])
                    promises.push(archive(putCode));            
            
            $q.all(promises).then(function(){
                serv.bulkArchiveMap.length = 0;
                serv.bulkChecked = false;
                serv.reloadNotifications();
            });
            
        },
        checkSelection: function(){
            var count = 0;
            var totalNotifications = 0;            
            serv.selectionActive = false;
            for (putCode in serv.bulkArchiveMap){                
                if(serv.bulkArchiveMap[putCode] == true){
                    serv.selectionActive = true;
                    count++;
                }
            }                      
            for (i = 0; i < serv.notifications.length; i++)                
                if (serv.notifications[i].archivedDate == null)
                    totalNotifications++;            
            
            totalNotifications == count ? serv.bulkChecked = true : serv.bulkChecked = false;
        }
    };
    return serv;
}]);