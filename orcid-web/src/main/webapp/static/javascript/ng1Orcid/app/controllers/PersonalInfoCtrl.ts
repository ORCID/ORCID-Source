//Migrated

declare var orcidVar: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module

export const PersonalInfoCtrl = angular.module('orcidApp').controller(
    'PersonalInfoCtrl', 
    [
        '$compile', 
        '$scope', 
        'utilsService', 
        'workspaceSrvc', 
        function (
            $compile, 
            $scope, 
            utilsService,
            workspaceSrvc 
        ){

            var lastModifiedTimeString = orcidVar.lastModified.replace(/,|\./g , "");

            $scope.displayInfo = workspaceSrvc.displayPersonalInfo;
            $scope.lastModifiedDate = utilsService.formatTime(Number(lastModifiedTimeString));

            $scope.toggleDisplayInfo = function () {
                $scope.displayInfo = !$scope.displayInfo;
            };

            console.log(orcidVar);

        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class PersonalInfoCtrlNg2Module {}