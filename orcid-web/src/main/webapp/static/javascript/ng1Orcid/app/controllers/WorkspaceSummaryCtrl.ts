import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module

export const WorkspaceSummaryCtrl = angular.module('orcidApp').controller(
    'WorkspaceSummaryCtrl', 
    [
        '$compile', 
        '$scope', 
        'affiliationsSrvc', 
        'fundingSrvc', 
        'peerReviewSrvc', 
        'workspaceSrvc',
        'worksSrvc', 
        function (
            $compile, 
            $scope, 
            affiliationsSrvc, 
            fundingSrvc, 
            peerReviewSrvc, 
            workspaceSrvc,
            worksSrvc
        ){
            $scope.affiliationsSrvc = affiliationsSrvc;
            $scope.fundingSrvc = fundingSrvc;
            $scope.peerReviewSrvc = peerReviewSrvc;
            $scope.workspaceSrvc = workspaceSrvc;
            $scope.worksSrvc = worksSrvc;
            
            $scope.showAddAlert = function () {
                if (worksSrvc.loading == false 
                    && affiliationsSrvc.loading == false 
                    && peerReviewSrvc.loading == false
                    && worksSrvc.groups.length == 0
                    && affiliationsSrvc.educations.length == 0
                    && affiliationsSrvc.employments.length == 0
                    && fundingSrvc.groups.length == 0
                    && peerReviewSrvc.groups.lenght == 0) 
                {    
                    return true;
                }
                return false;
            };
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class WorkspaceSummaryCtrlNg2Module {}