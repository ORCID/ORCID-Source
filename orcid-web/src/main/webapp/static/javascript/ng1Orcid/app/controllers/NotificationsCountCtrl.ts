declare var orcidVar: any;

// Controller for notifications
import * as angular from 'angular';
import {NgModule} from '@angular/core';

// Controller to show alert for unread notifications
export const NotificationsCountCtrl = angular.module('orcidApp').controller(
    'NotificationsCountCtrl',
    [
        '$compile', 
        '$scope', 
        'notificationsSrvc', 
        function (
            $compile, 
            $scope, 
            notificationsSrvc
        ){
            $scope.getUnreadCount = notificationsSrvc.getUnreadCount;
    
            $scope.isCurrentPage = function(path){
                return window.location.href.startsWith(orcidVar.baseUri + '/' + path);
            }
        
            // Pages that load notifications will get the unread count themselves
            if(!($scope.isCurrentPage('my-orcid') || $scope.isCurrentPage('inbox'))){
                notificationsSrvc.retrieveUnreadCount();
            }
    
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class NotificationsCountCtrlNg2Module {}