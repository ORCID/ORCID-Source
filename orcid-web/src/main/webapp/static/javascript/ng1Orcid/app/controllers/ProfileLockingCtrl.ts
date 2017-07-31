declare var $: any;
declare var colorbox: any;
declare var getBaseUri: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const profileLockingCtrl = angular.module('orcidApp').controller(
    'profileLockingCtrl', 
    [
        '$compile', 
        '$scope', 
        function(
            $compile,
            $scope
        ){
            $scope.orcidToLock = '';
            $scope.orcidToUnlock = '';
            $scope.showLockModal = false;
            $scope.showUnlockModal = false;
            
            $scope.closeModal = function() {        
                $.colorbox.close();
            };

            $scope.getLockReasons = function() {
                $.ajax({
                    url: getBaseUri()+'/admin-actions/lock-reasons.json',
                    dataType: 'json',
                    success: function(data){
                        $scope.lockReasons = data;
                        $scope.lockReason = $scope.lockReasons[0];
                        $scope.$apply();
                    }
                }).fail(function(error) {
                    // something bad is happening!
                    console.log("Error while fetching lock reasons");
                });
            };

            $scope.lockAccount = function() {
                $.ajax({
                    url: getBaseUri()+'/admin-actions/lock-accounts.json',
                    type: 'POST',
                    data: angular.toJson({ orcidsToLock: $scope.orcidToLock, lockReason: $scope.lockReason, description: $scope.description }),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data){
                        $scope.result = data;
                        $scope.orcidToLock = '';
                        $scope.description = '';
                        $scope.getLockReasons();
                        $scope.$apply();
                    }
                }).fail(function(error) {
                    // something bad is happening!
                    console.log("Error while locking account");
                });
            };

            $scope.toggleLockModal = function(){
                $scope.showLockModal = !$scope.showLockModal;
                $('#lock_modal').toggle();
            };
            
            $scope.toggleUnlockModal = function(){
                $scope.showUnlockModal = !$scope.showUnlockModal;
                $('#unlock_modal').toggle();
            };

            $scope.unlockAccount = function() {
                $.ajax({
                    url: getBaseUri()+'/admin-actions/unlock-accounts.json',
                    type: 'POST',
                    data: $scope.orcidToUnlock,
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data){   
                        $scope.result = data;               
                        $scope.orcidToUnlock = '';
                        $scope.$apply();
                    }
                }).fail(function(error) {
                    // something bad is happening!
                    console.log("Error while unlocking account");
                });
            };
            
            $scope.getLockReasons();
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class profileLockingCtrlNg2Module {}