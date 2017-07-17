declare var getBaseUri: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const DeactivateProfileCtrl = angular.module('orcidApp').controller(
    'DeactivateProfileCtrl', 
    [
        '$scope', 
        function (
            $scope
        ) {
            $scope.orcidsToDeactivate = "";
            $scope.showSection = false;

            $scope.deactivateOrcids = function() {
                $.ajax({
                    url: getBaseUri()+'/admin-actions/deactivate-profiles.json',
                    type: 'POST',
                    dataType: 'json',
                    data: $scope.orcidsToDeactivate,
                    contentType: 'application/json;charset=UTF-8',
                    success: function(data){
                        $scope.$apply(function(){
                            $scope.result = data;
                        });
                    }
                }).fail(function(error) {
                    // something bad is happening!
                    console.log("Error re-sending claim emails");
                });
            };

            $scope.toggleSection = function(){
                $scope.showSection = !$scope.showSection;
                $('#deactivation_modal').toggle();
            };

        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class DeactivateProfileCtrlNg2Module {}