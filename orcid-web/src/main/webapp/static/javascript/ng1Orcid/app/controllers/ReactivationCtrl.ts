declare var getBaseUri: any;
declare var orcidVar: any;
declare var trimAjaxFormText: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const ReactivationCtrl = angular.module('orcidApp').controller(
    'ReactivationCtrl', 
    [
        '$compile', 
        '$scope', 
        'commonSrvc', 
        'vcRecaptchaService', 
        function (
            $compile, 
            $scope, 
            commonSrvc, 
            vcRecaptchaService
        ) {
    
            $scope.privacyHelp = {};

            $scope.getReactivation = function(resetParams, linkFlag){
                $.ajax({
                    url: getBaseUri() + '/register.json',
                    dataType: 'json',
                    success: function(data) {
                       $scope.register = data;
                       $scope.register.resetParams = resetParams;
                       $scope.register.activitiesVisibilityDefault.visibility = null;
                       $scope.$apply();               
            
                       $scope.$watch('register.givenNames.value', function() {
                           trimAjaxFormText($scope.register.givenNames);
                       }); // initialize the watch
            
                       $scope.$watch('register.familyNames.value', function() {
                            trimAjaxFormText($scope.register.familyNames);
                       }); // initialize the watch
                    }
                }).fail(function(){
                // something bad is happening!
                    console.log("error fetching register.json");
                });
            };

            $scope.isValidClass = function (cur) {
                var valid;
                if (cur === undefined) {
                    return '';
                } 
                valid = true;
                if (cur.required && (cur.value == null || cur.value.trim() == '')) {
                    valid = false;
                }
                if (cur.errors !== undefined && cur.errors.length > 0) {
                    valid = false;
                }
                return valid ? '' : 'text-error';
            };

            $scope.postReactivationConfirm = function () {
                $scope.register.valNumClient = $scope.register.valNumServer / 2;
                var baseUri = getBaseUri();
                if($scope.register.linkType === 'shibboleth'){
                    baseUri += '/shibboleth';
                }
                $.ajax({
                    url: baseUri + '/reactivationConfirm.json',
                    type: 'POST',
                    data:  angular.toJson($scope.register),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        if(data.errors.length == 0){
                            window.location.href = data.url;
                        }
                        else{
                            $scope.register = data;
                            $scope.$apply();
                        }
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("ReactivationCtrl.postReactivationConfirm() error");
                });
            };

            $scope.serverValidate = function (field) {        
                if (field === undefined) {
                    field = '';
                } 
                $.ajax({
                    url: getBaseUri() + '/register' + field + 'Validate.json',
                    type: 'POST',
                    data:  angular.toJson($scope.register),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        commonSrvc.copyErrorsLeft($scope.register, data);
                        $scope.$apply();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("serverValidate() error");
                });
            };

            $scope.toggleClickPrivacyHelp = function(key) {
                if ( document.documentElement.className.indexOf('no-touch') == -1 ) {
                    $scope.privacyHelp[key]=!$scope.privacyHelp[key];
                }
            };

            $scope.updateActivitiesVisibilityDefault = function(priv, $event) {
                $scope.register.activitiesVisibilityDefault.visibility = priv;
            };  
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class ReactivationCtrlNg2Module {}