declare var printWindow: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module

// Controller to show alert for unread notifications
export const PrintRecordCtrl = angular.module('orcidApp').controller(
    'PrintRecordCtrl',
    [
        '$compile', 
        '$scope', 
        '$window', 
        function (
            $compile, 
            $scope, 
            $window
        ) {

            $scope.printRecord = function(url){
                //open window
                printWindow = $window.open(url);  
            }

        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class PrintRecordCtrlNg2Module {}