declare var getBaseUri: any;
declare var logAjaxError: any;

import * as angular from 'angular';
import { FormsModule }   from '@angular/forms'; // <-- NgModel lives here
import { NgModule } from '@angular/core';

// This is the Angular 1 part of the module
export const BiographyCtrl = angular.module('orcidApp').controller(
    'BiographyCtrl',
    [
        '$scope',
        '$rootScope',
        '$compile',
        'emailSrvc',
        'initialConfigService', 
        function (
            $scope, 
            $rootScope, 
            $compile, 
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

// This is the Angular 2 part of the module
@NgModule({})
export class BiographyCtrlNg2Module {}