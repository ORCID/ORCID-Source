declare var iframeResize: any;

// Controller for notifications
import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const NotificationsCtrl = angular.module('orcidApp').controller('NotificationsCtrl',['$scope', '$compile', 'notificationsSrvc', function ($scope, $compile, notificationsSrvc){
    $scope.displayBody = {};
    notificationsSrvc.displayBody = {};    
    $scope.notificationsSrvc = notificationsSrvc;
    $scope.notifications = notificationsSrvc.notifications;
    $scope.showMore = notificationsSrvc.showMore;
    $scope.areMore = notificationsSrvc.areMore;
    $scope.archive = notificationsSrvc.archive;
    $scope.getNotifications = notificationsSrvc.getNotifications;
    $scope.reloadNotifications = notificationsSrvc.reloadNotifications;
    $scope.bulkChecked = notificationsSrvc.bulkChecked;
    $scope.bulkArchiveMap = notificationsSrvc.bulkArchiveMap;
    $scope.toggleDisplayBody = function (notificationId) {
        $scope.displayBody[notificationId] = !$scope.displayBody[notificationId];        
        notificationsSrvc.displayBody[notificationId] = $scope.displayBody[notificationId]; 
        notificationsSrvc.flagAsRead(notificationId);
        iframeResize(notificationId);
    };    
    
    $scope.$watch(function () { return notificationsSrvc.bulkChecked }, function (newVal, oldVal) {
        if (typeof newVal !== 'undefined') {
            $scope.bulkChecked = notificationsSrvc.bulkChecked;
        }
    });

    notificationsSrvc.getNotifications();
        
}]);

// This is the Angular 2 part of the module
@NgModule({})
export class NotificationsCtrlNg2Module {}