declare var iframeResize: any;

// Controller for notifications
import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const NotificationsCtrl = angular.module('orcidApp').controller(
    'NotificationsCtrl',
    [
        '$scope', 
        '$compile', 
        'notificationsSrvc', 
        function (
            $scope, 
            $compile, 
            notificationsSrvc
        ){
            notificationsSrvc.displayBody = {};    
            $scope.notificationsSrvc = notificationsSrvc;

            $scope.archive = $scope.notificationsSrvc.archive;
            $scope.areMore = $scope.notificationsSrvc.areMore;
            $scope.bulkArchiveMap = $scope.notificationsSrvc.bulkArchiveMap;
            $scope.bulkChecked = $scope.notificationsSrvc.bulkChecked;
            $scope.displayBody = {};
            $scope.getNotifications = $scope.notificationsSrvc.getNotifications;
            $scope.notifications = $scope.notificationsSrvc.notifications;
            $scope.reloadNotifications = $scope.notificationsSrvc.reloadNotifications;
            $scope.showMore = $scope.notificationsSrvc.showMore;
            
            $scope.$watch(function () { 
                return $scope.notificationsSrvc.bulkChecked }, 
                function (newVal, oldVal) {
                    if (typeof newVal !== 'undefined') {
                        $scope.bulkChecked = $scope.notificationsSrvc.bulkChecked;
                    }
                }
            );

            $scope.toggleDisplayBody = function (notificationId) {
                $scope.displayBody[notificationId] = !$scope.displayBody[notificationId];        
                $scope.notificationsSrvc.displayBody[notificationId] = $scope.displayBody[notificationId]; 
                $scope.notificationsSrvc.flagAsRead(notificationId);
                iframeResize(notificationId);
            };    

            $scope.notificationsSrvc.getNotifications();
                
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class NotificationsCtrlNg2Module {}