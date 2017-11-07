declare var getBaseUri: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module

export const _Social2FACtrl = angular.module('orcidApp').controller(
    'Social2FACtrl', 
    [
        '$compile', 
        '$scope', 
        function (
            $compile,
            $scope
        ) {
    
            $scope.init = function() {
                $('#enterRecoveryCode').click(function() {
                    $('#recoveryCodeSignin').show();
                });
                
                $.ajax({
                    url: getBaseUri() + '/social/2FA/authenticationCode.json',
                    dataType: 'json',
                    success: function(data) {
                        $scope.codes = data;
                    }
                }).fail(function() {
                    console.log("An error occurred getting 2FA codes wrapper");
                });
            };

            $scope.submitCode = function() {
                $.ajax({
                    url: getBaseUri() + '/social/2FA/submitCode.json',
                    dataType: 'json',
                    data: angular.toJson($scope.codes),
                    contentType: 'application/json;charset=UTF-8',
                    type: 'POST',
                    success: function(data) {               
                        $scope.codes = data;
                        if (data.errors.length == 0) {
                            window.location.href = data.redirectUrl;
                        } else {
                            $scope.verificationCode = "";
                            $scope.recoveryCode = "";
                        }
                    }
                }).fail(function() {
                    console.log("An error occurred submitting 2FA code");
                });
            };
            
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class _Social2FACtrlNg2Module {}