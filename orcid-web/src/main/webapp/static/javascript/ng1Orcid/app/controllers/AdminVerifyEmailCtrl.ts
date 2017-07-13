declare var getBaseUri: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const adminVerifyEmailCtrl = angular.module('orcidApp').controller(
    'adminVerifyEmailCtrl',
    [
        '$compile', 
        '$scope',
        function (
            $compile,
            $scope
        ){
            $scope.showSection = false;

            $scope.toggleSection = function(){
                $scope.showSection = !$scope.showSection;
                $('#verify_email_section').toggle();
            };

            $scope.verifyEmail = function(){
                $.ajax({
                    url: getBaseUri()+'/admin-actions/admin-verify-email.json',
                    type: 'POST',
                    dataType: 'text',
                    data: $scope.email,
                    contentType: 'application/json;charset=UTF-8',
                    success: function(data){
                        $scope.$apply(function(){
                            $scope.result = data;
                        });
                    }
                }).fail(function(error) {
                    // something bad is happening!
                    console.log("Error verifying the email address");
                });
            };
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class adminVerifyEmailCtrlNg2Module {}