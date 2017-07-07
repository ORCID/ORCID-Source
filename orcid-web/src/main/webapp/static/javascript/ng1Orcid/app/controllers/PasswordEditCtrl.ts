declare var getBaseUri: any;
declare var fixZindexIE7: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const PasswordEditCtrl = angular.module('orcidApp').controller(
    'PasswordEditCtrl', 
    [
        '$http', 
        '$scope', 
        function (
            $http,
            $scope 
        ) {
            $scope.getChangePassword = function() {
                $.ajax({
                    url: getBaseUri() + '/account/change-password.json',
                    dataType: 'json',
                    success: function(data) {
                        $scope.changePasswordPojo = data;
                        $scope.$apply();
                        $scope.zIndexfixIE7();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("error with change password");
                });
            };

            $scope.saveChangePassword = function() {
                $.ajax({
                    url: getBaseUri() + '/account/change-password.json',
                    type: 'POST',
                    data: angular.toJson($scope.changePasswordPojo),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        $scope.changePasswordPojo = data;
                        $scope.$apply();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("error with edit password");
                });
            };

            $scope.zIndexfixIE7 = function(){
                fixZindexIE7('#password-edit', 999999);
                fixZindexIE7('#password-edit .relative', 99999);
            };

            $scope.getChangePassword();
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class PasswordEditCtrlNg2Module {}