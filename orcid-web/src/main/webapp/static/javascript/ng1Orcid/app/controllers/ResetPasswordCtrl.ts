declare var getBaseUri: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const ResetPasswordCtrl = angular.module('orcidApp').controller(
    'ResetPasswordCtrl', 
    [
        '$compile', 
        '$scope', 
        'commonSrvc',
        function (
            $compile, 
            $scope, 
            commonSrvc
        ) {
            $scope.getResetPasswordForm = function() {
                $.ajax({
                    url: getBaseUri() + '/password-reset.json',
                    dataType: 'json',
                    success: function(data) {
                        $scope.resetPasswordForm = data;
                        $scope.$apply();
                    }
                }).fail(function(){
                // something bad is happening!
                    console.log("error fetching password-reset.json");
                });
            };

            $scope.postPasswordReset = function() {
                var urlParts = window.location.href.split('/');
                var encryptedEmail = urlParts[urlParts.length -1];
                $scope.resetPasswordForm.encryptedEmail = encryptedEmail;
                $.ajax({
                    url: getBaseUri() + '/reset-password-email.json',
                    type: 'POST',
                    data:  angular.toJson($scope.resetPasswordForm),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        if (data.successRedirectLocation != null) {
                            window.location.href = data.successRedirectLocation;
                        } else {
                            commonSrvc.copyErrorsLeft($scope.resetPasswordForm, data);
                            $scope.$apply();
                        }
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("error posting to reset-password-email.json");
                });
            };

            $scope.serverValidate = function () {
                $.ajax({
                    url: getBaseUri() + '/reset-password-form-validate.json',
                    type: 'POST',
                    data:  angular.toJson($scope.resetPasswordForm),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        commonSrvc.copyErrorsLeft($scope.resetPasswordForm, data);
                        $scope.$apply();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("ResetPasswordCtrl.serverValidate() error");
                });
            };
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class ResetPasswordCtrlNg2Module {}