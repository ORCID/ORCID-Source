declare var getBaseUri: any;
declare var orcid: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const adminDelegatesCtrl = angular.module('orcidApp').controller(
    'adminDelegatesCtrl',
    [
        '$scope',
        function (
            $scope
        ){
            $scope.managed_verified = false;
            $scope.request = {trusted : {errors: [], value: ''}, managed : {errors: [], value: ''}};
            $scope.showSection = false;
            $scope.success = false;
            $scope.trusted_verified = false;

            $scope.checkClaimedStatus = function (whichField){
                var orcidOrEmail = '';
                if(whichField == 'trusted') {
                    $scope.trusted_verified = false;
                    orcidOrEmail = $scope.request.trusted.value;
                } else {
                    $scope.managed_verified = false;
                    orcidOrEmail = $scope.request.managed.value;
                }

                $.ajax({
                    url: getBaseUri()+'/admin-actions/admin-delegates/check-claimed-status.json?orcidOrEmail=' + orcidOrEmail,
                    type: 'GET',
                    dataType: 'json',
                    success: function(data){
                            if(data) {
                                if(whichField == 'trusted') {
                                    $scope.trusted_verified = true;
                                } else {
                                    $scope.managed_verified = true;
                                }
                                $scope.$apply();
                            }
                        }
                    }).fail(function(error) {
                        // something bad is happening!
                        console.log("Error getting account details for: " + orcid);
                    });
            };

            $scope.confirmDelegatesProcess = function() {
                $scope.success = false;
                $.ajax({
                    url: getBaseUri()+'/admin-actions/admin-delegates',
                    type: 'POST',
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    data: angular.toJson($scope.request),
                    success: function(data){
                        $scope.request = data;
                        if(data.successMessage) {
                            $scope.success = true;
                        }
                        $scope.$apply();
                    }
                }).fail(function(error) {
                    // something bad is happening!
                    console.log("Error getting delegates request");
                });
            };

            $scope.toggleSection = function(){
                $scope.showSection = !$scope.showSection;
                $('#delegates_section').toggle();
            };

        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class adminDelegatesCtrlNg2Module {}