declare var printWindow: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const AffiliationCtrl = angular.module('orcidApp').controller(
    'PublicRecordCtrl',
    [
        '$compile', 
        '$scope', 
        '$window', 
        function (
            $compile, 
            $scope, 
            $window
        ) {
            $scope.showPopover = new Array();
            $scope.showSources = new Array();

            $scope.hidePopover = function(section){
                $scope.showPopover[section] = false;    
            };

            $scope.printRecord = function(url){
                //open window
                printWindow = $window.open(url);  
            };

            $scope.showPopover = function(section){
                $scope.showPopover[section] = true;
            }; 

            $scope.toggleSourcesDisplay = function(section){        
                $scope.showSources[section] = !$scope.showSources[section];     
            };

        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class AffiliationCtrlNg2Module {}