declare var orcidVar: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module

export const PersonalInfoCtrl = angular.module('orcidApp').controller('PersonalInfoCtrl', ['$scope', '$compile', 'workspaceSrvc', 'utilsService', function ($scope, $compile, workspaceSrvc, utilsService){
    $scope.displayInfo = workspaceSrvc.displayPersonalInfo;

    $scope.toggleDisplayInfo = function () {
        $scope.displayInfo = !$scope.displayInfo;
    };

    var lastModifiedTimeString = orcidVar.lastModified.replace(/,/g , "");
    $scope.lastModifiedDate = utilsService.formatTime(Number(lastModifiedTimeString));
}]);

// This is the Angular 2 part of the module
@NgModule({})
export class PersonalInfoCtrlNg2Module {}