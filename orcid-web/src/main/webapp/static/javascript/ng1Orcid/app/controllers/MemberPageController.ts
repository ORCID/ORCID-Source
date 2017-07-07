import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module

export const MemberPageController = angular.module('orcidApp').controller(
    'MemberPageController',
    [
        '$sce', 
        '$scope', 
        'membersListSrvc', 
        function (
            $sce, 
            $scope, 
            membersListSrvc
        ){
            $scope.membersListSrvc = membersListSrvc;
            
            $scope.renderHtml = function (htmlCode) {
                return $sce.trustAsHtml(htmlCode);
            };
            
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class MemberPageControllerNg2Module {}