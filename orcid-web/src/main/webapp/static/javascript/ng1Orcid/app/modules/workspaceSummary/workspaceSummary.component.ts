declare var ActSortState: any;
declare var GroupedActivities: any;
declare var om: any;
declare var openImportWizardUrl: any;

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

@Component({
    selector: 'work-summary-ng2',
    template:  scriptTmpl("work-summary-ng2-template")
})
export class WorkSpaceSummaryComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    constructor( 
    ) {
        //Create global flag to see if alert can be added
        /*
        $scope.affiliationsSrvc = affiliationsSrvc;
        $scope.fundingSrvc = fundingSrvc;
        $scope.peerReviewSrvc = peerReviewSrvc;
        $scope.workspaceSrvc = workspaceSrvc;
        $scope.worksSrvc = worksSrvc;
        */
    }

    
    showAddAlert(): boolean {
        /*
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
        */
        return false;
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        
    };
}

/*
//Migrated

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
*/