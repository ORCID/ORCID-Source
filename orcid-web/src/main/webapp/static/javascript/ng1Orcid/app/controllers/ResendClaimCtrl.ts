declare var getBaseUri: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const ResendClaimCtrl = angular.module('orcidApp').controller(
    'ResendClaimCtrl', 
    [
        '$scope', 
        function (
            $scope
        ) {
            $scope.emailIds = "";
            $scope.showSection = false;

            $scope.resendClaimEmails = function() {
                $.ajax({
                    url: getBaseUri()+'/admin-actions/resend-claim.json',
                    type: 'POST',
                    dataType: 'json',
                    data: $scope.emailIds,
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
                $('#batch_resend_section').toggle();
            };
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class ResendClaimCtrlNg2Module {}