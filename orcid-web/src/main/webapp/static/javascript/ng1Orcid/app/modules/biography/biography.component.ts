declare var $scope: any;
declare var getBaseUri: any;
declare var logAjaxError: any;

import * as angular from 'angular';
import { NgModule } from '@angular/core';

export class BiographyCtrl {
    biographyForm: any;
    configuration: any;
    emails: any;
    emailSrvc: any;
    emailVerified: any;
    lengthError: any;
    showEdit: any;
    showElement: any;

    static $inject = [
        '$compile',
        '$rootScope',
        '$scope',
        'emailSrvc',
        'initialConfigService',
    ];

    constructor(
        $compile, 
        $rootScope, 
        $scope, 
        emailSrvc, 
        initialConfigService 
    ) {
        //console.log('BiographyCtrl loaded');
        this.biographyForm = null;
        this.configuration = initialConfigService.getInitialConfiguration();;
        this.emails = {};
        this.emailSrvc = emailSrvc;
        this.emailVerified = false;
        this.lengthError = false;
        this.showEdit = false;
        this.showElement = {};
        
        /*
        console.log('emailSrvc', emailSrvc);
        this.emailSrvc.getEmails(
            function(data) {
                console.log('data', data);
                this.emails = data;//.emails;
                if( this.emails && this.emails['emails'] ){
                    this.emails = this.emails['emails'];
                }
                console.log( 'this.emails', this.emails );
                if( this.emailSrvc.getEmailPrimary().verified == true ) {
                    this.emailVerified = true;
                }
            }
        );*/

        this.getBiographyForm();
    }

    /////////////////////// Begin of verified email logic for work
    showEmailVerificationModal(): void{
        /*$rootScope.$broadcast(
            'emailVerifiedObj', 
            {
                flag: emailVerified, 
                emails: emails
            }
        );*/
    };

    cancel(): void {
        this.getBiographyForm();
        console.log('cancel');
        this.showEdit = false;
    };

    checkLength(): void {
        if (this.biographyForm != null){
            if (this.biographyForm.biography != null){
                if (this.biographyForm.biography.value != null){    
                    if (this.biographyForm.biography.value.length > 5000) {
                        this.lengthError = true;
                    } else {
                        this.lengthError = false;
                    }
                }
            }
        }
        return this.lengthError;
    };

    close(): void {
        this.showEdit = false;
    };

    getBiographyForm(): void {
        console.log('getBiographyForm');
        $.ajax({
            url: getBaseUri() + '/account/biographyForm.json',
            dataType: 'json',
            success: function(data) {
                this.biographyForm = data;
                //$scope.$apply();
            }
        }).fail(function(e){
            // something bad is happening!
            console.log("error fetching BiographyForm");
            logAjaxError(e);
        });
    };

    hideTooltip(tp): void{
        this.showElement[tp] = false;
    };

    setBiographyForm(): void{
        if( this.checkLength() ){    
            return; // do nothing if there is a length error
        } 
        $.ajax({
            contentType: 'application/json;charset=UTF-8',
            data:  angular.toJson(this.biographyForm),
            dataType: 'json',
            type: 'POST',
            url: getBaseUri() + '/account/biographyForm.json',
            success: function(data) {
                this.biographyForm = data;
                if(data.errors.length == 0){
                    this.close();
                }
                //$scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("BiographyCtrl.serverValidate() error");
        });
    };

    setPrivacy(priv, $event): void {
        $event.preventDefault();
        this.biographyForm.visiblity.visibility = priv;
        this.setBiographyForm();        
    };

    showTooltip(tp): void {
        this.showElement[tp] = true;
    };
    
    toggleEdit(): void {
        if(this.emailVerified === true || this.configuration.showModalManualEditVerificationEnabled == false){
            this.showEdit = !this.showEdit;
        }else{
            this.showEmailVerificationModal();
        }
    };

    //this.getBiographyForm();
    
    /////////////////////// End of verified email logic for work

}

export const biographyCmp = {
    /*
    template : './biography.component.html',
    */
    controller: BiographyCtrl,
    controllerAs: 'ctrl'
};

/*
BiographyModule.controller(
    'BiographyCtrl',
    [
        '$scope',
        '$rootScope',
        //'$compile',
        'emailSrvc',
        'initialConfigService', 
        function (
            $scope, 
            $rootScope, 
            //$compile, 
            emailSrvc, 
            initialConfigService
        ) {
            $scope.biographyForm = null;
            $scope.emailSrvc = emailSrvc;
            $scope.lengthError = false;
            $scope.showEdit = false;
            $scope.showElement = {};

            /////////////////////// Begin of verified email logic for work
            var configuration = initialConfigService.getInitialConfiguration();;
            var emails = {};
            var emailVerified = false;

            var showEmailVerificationModal = function(){
                $rootScope.$broadcast('emailVerifiedObj', {flag: emailVerified, emails: emails});
            };
            
            $scope.emailSrvc.getEmails(
                function(data) {
                    emails = data.emails;
                    if( $scope.emailSrvc.getEmailPrimary().verified == true ) {
                        emailVerified = true;
                    }
                }
            );
            /////////////////////// End of verified email logic for work

            $scope.cancel = function() {
                $scope.getBiographyForm();
                $scope.showEdit = false;
            };

            $scope.checkLength = function () {
                if ($scope.biographyForm != null){
                    if ($scope.biographyForm.biography != null){
                        if ($scope.biographyForm.biography.value != null){    
                            if ($scope.biographyForm.biography.value.length > 5000) {
                                $scope.lengthError = true;
                            } else {
                                $scope.lengthError = false;
                            }
                        }
                    }
                }
                return $scope.lengthError;
            };

            $scope.close = function() {
                $scope.showEdit = false;
            };

            $scope.getBiographyForm = function(){
                $.ajax({
                    url: getBaseUri() + '/account/biographyForm.json',
                    dataType: 'json',
                    success: function(data) {
                        $scope.biographyForm = data;
                        $scope.$apply();
                    }
                }).fail(function(e){
                    // something bad is happening!
                    console.log("error fetching BiographyForm");
                    logAjaxError(e);
                });
            };

            $scope.hideTooltip = function(tp){
                $scope.showElement[tp] = false;
            };

            $scope.setBiographyForm = function(){
                if( $scope.checkLength() ){    
                    return; // do nothing if there is a length error
                } 
                $.ajax({
                    contentType: 'application/json;charset=UTF-8',
                    data:  angular.toJson($scope.biographyForm),
                    dataType: 'json',
                    type: 'POST',
                    url: getBaseUri() + '/account/biographyForm.json',
                    success: function(data) {
                        $scope.biographyForm = data;
                        if(data.errors.length == 0){
                            $scope.close();
                        }
                        $scope.$apply();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("BiographyCtrl.serverValidate() error");
                });
            };

            $scope.setPrivacy = function(priv, $event) {
                $event.preventDefault();
                $scope.biographyForm.visiblity.visibility = priv;
                $scope.setBiographyForm();        
            };

            $scope.showTooltip = function(tp){
                $scope.showElement[tp] = true;
            };
            
            $scope.toggleEdit = function() {
                if(emailVerified === true || configuration.showModalManualEditVerificationEnabled == false){
                    $scope.showEdit = !$scope.showEdit;
                }else{
                    showEmailVerificationModal();
                }
            };

            $scope.getBiographyForm();
        }
    ]
);
*/