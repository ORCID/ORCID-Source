//Migrated

declare var getBaseUri: any;
declare var orcidGA: any;
declare var orcidVar: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const ClaimCtrl = angular.module('orcidApp').controller(
    'ClaimCtrl', 
    [
        '$compile', 
        '$scope', 
        'commonSrvc', 
        function (
            $compile, 
            $scope, 
            commonSrvc
        ) {
            $scope.postingClaim = false;
            
            $scope.getClaim = function(){
                $.ajax({
                    url: $scope.getClaimAjaxUrl(),
                    dataType: 'json',
                    success: function(data) {
                        $scope.register = data;
                        $scope.register.activitiesVisibilityDefault.visibility = null;
                        $scope.$apply();
                    }
                }).fail(function(){
                    // something bad is happening!
                    console.log("error fetching register.json");
                });
            };

            $scope.getClaimAjaxUrl = function () {
                return window.location.href.split("?")[0]+".json";
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

            $scope.postClaim = function () {
                if ($scope.postingClaim) {
                    return;
                }
                $scope.postingClaim = true;
                $.ajax({
                    url: $scope.getClaimAjaxUrl(),
                    type: 'POST',
                    data:  angular.toJson($scope.register),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        $scope.register = data;

                        if ($scope.register.errors.length == 0) {
                            if ($scope.register.url != null) {
                                orcidGA.gaPush(['send', 'event', 'RegGrowth', 'New-Registration', 'Website']);
                                orcidGA.windowLocationHrefDelay($scope.register.url);
                            }
                        }
                        $scope.postingClaim = false;
                        $scope.$apply();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("postClaim() error");
                    $scope.postingClaim = false;
                });
            };

            $scope.serverValidate = function (field) {
                if (field === undefined) {
                    field = '';
                }
                $.ajax({
                    url: getBaseUri() + '/claim' + field + 'Validate.json',
                    type: 'POST',
                    data:  angular.toJson($scope.register),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        // alert(angular.toJson(data));
                        commonSrvc.copyErrorsLeft($scope.register, data);
                        $scope.$apply();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("serverValidate() error");
                });
            };

            $scope.updateActivitiesVisibilityDefault = function(priv, $event) {
                $scope.register.activitiesVisibilityDefault.visibility = priv;
            };

            // init
            $scope.getClaim();
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class ClaimCtrlNg2Module {}