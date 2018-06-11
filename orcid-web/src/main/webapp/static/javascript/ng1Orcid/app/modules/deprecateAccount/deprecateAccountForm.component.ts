//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { 
    GenericService } 
    from '../../shared/generic.service.ts'; 


@Component({
    selector: 'deprecate-account-form-ng2',
    template:  scriptTmpl("deprecate-account-form-ng2-template")
})
export class DeprecateAccountFormComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
   
    constructor(
        private deprecateProfileService: GenericService
    ) {

    }

 

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
//migrated

declare var $: any;
declare var colorbox: any;
declare var getBaseUri: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module

export const DeprecateAccountCtrl = angular.module('orcidApp').controller(
    'DeprecateAccountCtrl', 
    [
        '$compile', 
        '$rootScope', 
        '$scope', 
        'emailSrvc', 
        function (
            $compile, 
            $rootScope, 
            $scope, 
            emailSrvc
        ) {
            $scope.emailSrvc = emailSrvc;

            $scope.closeModal = function() {
                $.colorbox.close();
            };

            $scope.deprecateORCID = function() {
                $.ajax({
                    url: getBaseUri() + '/account/validate-deprecate-profile.json',
                    dataType: 'json',
                    data: angular.toJson($scope.deprecateProfilePojo),
                    type: 'POST',
                    contentType: 'application/json;charset=UTF-8',
                    success: function(data) {
                        $scope.deprecateProfilePojo = data;
                        if (data.errors.length > 0) {
                            $scope.$apply();
                        } else {
                            $.colorbox({
                                html : $compile($('#confirm-deprecate-account-modal').html())($scope),
                                escKey:false,
                                overlayClose:true,
                                close: '',
                                });
                        }
                        $scope.$apply();
                        $.colorbox.resize();
                    }
                }).fail(function() {
                    // something bad is happening!
                    //console.log("error with change DeactivateAccount");
                });
            };

            $scope.getDeprecateProfile = function() {
                $.ajax({
                    url: getBaseUri() + '/account/deprecate-profile.json',
                    dataType: 'json',
                    success: function(data) {
                        $scope.deprecateProfilePojo = data;
                        $scope.$apply();
                    }
                }).fail(function() {
                    //console.log("An error occurred preparing deprecate profile");
                });
            };

            $scope.submitModal = function() {
                $.ajax({
                    url: getBaseUri() + '/account/confirm-deprecate-profile.json',
                    type: 'POST',
                    data: angular.toJson($scope.deprecateProfilePojo),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {                
                        emailSrvc.getEmails(function(emailData) {
                            $rootScope.$broadcast('rebuildEmails', emailData);
                        });
                        $.colorbox({
                            html : $compile($('#deprecate-account-confirmation-modal').html())($scope),
                            escKey:false,
                            overlayClose:true,
                            close: '',
                            onClosed: function(){ $scope.deprecateProfilePojo = null; $scope.$apply(); },
                            });
                        $scope.$apply();
                        $.colorbox.resize();
                    }
                }).fail(function() {
                    // something bad is happening!
                    //console.log("error confirming account deprecation");
                });
            };
            
            $scope.getDeprecateProfile();
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class DeprecateAccountCtrlNg2Module {}
*/