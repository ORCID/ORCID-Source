declare var logAjaxError: any;
declare var getBaseUri: any;
declare var om: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const RequestPasswordResetCtrl = angular.module('orcidApp').controller(
    'RequestPasswordResetCtrl', 
    [
        '$compile', 
        '$scope', 
        '$timeout', 
        'utilsService', 
        function RequestPasswordResetCtrl(
            $compile, 
            $scope, 
            $timeout, 
            utilsService
        ) {

            $scope.getRequestResetPassword = function() {
                $.ajax({
                    url: getBaseUri() + '/reset-password.json',
                    dataType: 'json',
                    success: function(data) {
                        $scope.requestResetPassword = data;
                        $scope.$apply();
                    }
                }).fail(function(){
                    console.log("error getting reset-password.json");
                });  
            };

            $scope.postPasswordResetRequest = function() {
                $.ajax({
                    url: getBaseUri() + '/reset-password.json',
                    dataType: 'json',
                    type: 'POST',
                    data:  angular.toJson($scope.requestResetPassword),
                    contentType: 'application/json;charset=UTF-8',
                    success: function(data) {
                        $scope.requestResetPassword = data;
                        $scope.$apply();
                    }
                }).fail(function(){
                    console.log("error posting to /reset-password.json");
                });  
            };

            $scope.toggleResetPassword = function() {
                $scope.showResetPassword = !$scope.showResetPassword;

                // pre-populate with email from signin form 
                if(typeof $scope.userId != "undefined" 
                    && $scope.userId 
                    && utilsService.isEmail($scope.userId)
                ){
                    $scope.requestResetPassword = {
                        email:  $scope.userId
                    } 
                } else if (typeof $scope.authorizationForm != "undefined" 
                    && $scope.authorizationForm.userName.value 
                    && utilsService.isEmail($scope.authorizationForm.userName.value)
                ) {
                    $scope.requestResetPassword = {
                        email:  $scope.authorizationForm.userName.value
                    } 
                } else {
                    $scope.requestResetPassword = {
                        email:  ""
                    }
                }
            };

            // init reset password toggle text
            $scope.showResetPassword = (window.location.hash === "#resetPassword");
            $scope.resetPasswordToggleText = om.get("login.forgotten_password");
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class RequestPasswordResetCtrlNg2Module {}