//Migrated

// Controller for notifications

import * as angular from 'angular';
import {NgModule} from '@angular/core';

export const NotificationPreferencesCtrl = angular.module('orcidApp').controller(
    'NotificationPreferencesCtrl',
    [
        '$compile', 
        '$scope', 
        'emailSrvc', 
        'prefsSrvc', 
        function (
            $compile, 
            $scope, 
            emailSrvc, 
            prefsSrvc
        ) {
            $scope.prefsSrvc = prefsSrvc;
            $scope.emailSrvc = emailSrvc;
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class NotificationPreferencesCtrlNg2Module {}
