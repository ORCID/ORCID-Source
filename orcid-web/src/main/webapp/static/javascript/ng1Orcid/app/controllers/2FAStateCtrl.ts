declare var getBaseUri: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module

export const _2FAStateCtrl = angular.module('orcidApp').controller(
    '2FAStateCtrl', 
    [
        '$compile', 
        '$scope', 
        function (
            $compile,
            $scope
        ) {
    
            $scope.check2FAState = function() {
                $.ajax({
                    url: getBaseUri() + '/2FA/status.json',
                    dataType: 'json',
                    success: function(data) {
                        $scope.update2FAStatus(data);
                    }
                }).fail(function() {
                    console.log("An error occurred checking user's 2FA state");
                });
            };

            $scope.disable2FA = function() {
                $.ajax({
                    url: getBaseUri() + '/2FA/disable.json',
                    dataType: 'json',
                    type: 'POST',
                    success: function(data) {               
                        $scope.update2FAStatus(data);
                    }
                }).fail(function() {
                    console.log("An error occurred disabling user's 2FA");
                });
            };
            
            $scope.enable2FA = function() {
                window.location.href = getBaseUri() + '/2FA/setup';
            };

            $scope.update2FAStatus = function(status) {
                $scope.showEnabled2FA = status.enabled;
                $scope.showDisabled2FA = !status.enabled;
                $scope.$apply();
            };
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class _2FAStateCtrlNg2Module {}