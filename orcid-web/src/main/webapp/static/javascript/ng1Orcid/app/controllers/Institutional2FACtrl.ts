declare var getBaseUri: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module

export const _Institutional2FACtrl = angular.module('orcidApp').controller(
    'Institutional2FACtrl', 
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
                    url: getBaseUri() + '/shibboleth/2FA/authenticationCode.json',
                    dataType: 'json',
                    success: function(data) {
                        $scope.codes = data;
                    }
                }).fail(function(err) {
                    console.log("An error occurred getting 2FA codes wrapper: " +JSON.stringify(err));
                });
            };

            $scope.submitCode = function() {
                $.ajax({
                    url: getBaseUri() + '/shibboleth/2FA/submitCode.json',
                    dataType: 'json',
                    data: angular.toJson($scope.codes),
                    contentType: 'application/json;charset=UTF-8',
                    type: 'POST',
                    success: function(data) {               
                        $scope.codes = data;
                        if (data.errors.length == 0) {
                            $timeout(
                                function() {
                                    window.location.href = data.redirectUrl;
                                },
                                0
                            );
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
export class _Institutional2FACtrlNg2Module {}