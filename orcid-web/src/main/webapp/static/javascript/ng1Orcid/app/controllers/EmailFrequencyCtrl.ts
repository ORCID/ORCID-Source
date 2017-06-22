import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const EmailFrequencyCtrl = angular.module('orcidApp').controller(
    'EmailFrequencyCtrl',
    [
        '$scope', 
        '$compile', 
        'emailSrvc', 
        'prefsSrvc', 
        function (
            $scope, 
            $compile, 
            emailSrvc, 
            prefsSrvc
        ) {
            $scope.emailSrvc = emailSrvc;
            $scope.prefsSrvc = prefsSrvc;
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class EmailFrequencyCtrlNg2Module {}