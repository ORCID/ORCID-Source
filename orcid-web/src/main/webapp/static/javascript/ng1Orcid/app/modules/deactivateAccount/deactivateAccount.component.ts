declare var orcidGA: any;

//Import all the angular components

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

import { AccountService } 
    from '../../shared/account.service.ts'; 


@Component({
    selector: 'deactivate-account-ng2',
    template:  scriptTmpl("deactivate-account-ng2-template")
})
export class DeactivateAccountComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
   
    primaryEmail: string;

    constructor(
        private accountService: AccountService
    ) {
        this.primaryEmail = "";
    }

    closeModal(): void {
        //$.colorbox.close();
    };

    sendDeactivateEmail(): void {
        orcidGA.gaPush(['send', 'event', 'Disengagement', 'Deactivate_Initiate', 'Website']);
        this.accountService.sendDeactivateEmail()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.primaryEmail = data;
                /*
                $.colorbox({
                    html : $compile($('#deactivate-account-modal').html())($scope)
                });
                $.colorbox.resize();
                */
            },
            error => {
                //console.log('setformDataError', error);
            } 
        );
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

declare var $: any;
declare var colorbox: any;
declare var getBaseUri: any;
declare var orcidGA: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module

export const DeactivateAccountCtrl = angular.module('orcidApp').controller(
    'DeactivateAccountCtrl', 
    [
        '$compile', 
        '$scope', 
        function (
            $compile,
            $scope
        ) {
            $scope.closeModal = function() {
                $.colorbox.close();
            };

            $scope.sendDeactivateEmail = function() {
                orcidGA.gaPush(['send', 'event', 'Disengagement', 'Deactivate_Initiate', 'Website']);
                $.ajax({
                    url: getBaseUri() + '/account/send-deactivate-account.json',
                    dataType: 'text',
                    success: function(data) {
                        $scope.primaryEmail = data;
                        $.colorbox({
                            html : $compile($('#deactivate-account-modal').html())($scope)
                        });
                        $scope.$apply();
                        $.colorbox.resize();
                    }
                }).fail(function() {
                    // something bad is happening!
                    //console.log("error with change DeactivateAccount");
                });
            };
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class DeactivateAccountCtrlNg2Module {}
*/