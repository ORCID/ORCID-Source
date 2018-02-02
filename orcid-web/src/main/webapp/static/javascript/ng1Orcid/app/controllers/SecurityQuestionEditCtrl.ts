//Migrated

declare var $: any;
declare var colorbox: any;
declare var getBaseUri: any;
declare var orcidVar: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module

export const SecurityQuestionEditCtrl = angular.module('orcidApp').controller(
    'SecurityQuestionEditCtrl', 
    [
        '$compile', 
        '$scope', 
        function (
            $compile,
            $scope
        ) {
            $scope.errors = null;
            $scope.password = null;
            $scope.securityQuestions = [];

            $scope.checkCredentials = function() {
                $scope.password=null;
                if(orcidVar.isPasswordConfirmationRequired){
                    $.colorbox({
                        html: $compile($('#check-password-modal').html())($scope)
                    });
                    $.colorbox.resize();
                }
                else{
                    $scope.submitModal();
                }
            };

            $scope.closeModal = function() {
                $.colorbox.close();
            };

            $scope.getSecurityQuestion = function() {
                $.ajax({
                    url: getBaseUri() + '/account/security-question.json',
                    dataType: 'json',
                    success: function(data) {               
                        $scope.securityQuestionPojo = data;
                        $scope.$apply();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("error with security question.json");
                });
            };

            $scope.submitModal = function() {
                $scope.securityQuestionPojo.password=$scope.password;
                $.ajax({
                    url: getBaseUri() + '/account/security-question.json',
                    type: 'POST',
                    data: angular.toJson($scope.securityQuestionPojo),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        
                        if(data.errors.length != 0) {
                            $scope.errors=data.errors;
                        } else {
                            $scope.errors=null;
                        }
                        $scope.getSecurityQuestion();
                        $scope.$apply();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("error with security question");
                });
                $scope.password=null;
                $.colorbox.close();
            };

            $scope.getSecurityQuestion();

        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class SecurityQuestionEditCtrlNg2Module {}