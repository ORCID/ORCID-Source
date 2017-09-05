declare var getBaseUri: any;
declare var logAjaxError: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const SwitchUserCtrl = angular.module('orcidApp').controller(
    'SwitchUserCtrl',
    [
        '$compile', 
        '$document', 
        '$scope', 
        function (
            $compile, 
            $document,
            $scope
        ){
            $scope.isDroppedDown = false;
            $scope.searchResultsCache = new Object();

            $scope.getDelegates = function() {
                $.ajax({
                    url: getBaseUri() + '/delegators/delegators-and-me.json',
                    dataType: 'json',
                    success: function(data) {
                        $scope.delegators = data.delegators;
                        $scope.searchResultsCache[''] = $scope.delegators;
                        $scope.me = data.me;
                        $scope.unfilteredLength = $scope.delegators != null ? $scope.delegators.delegationDetails.length : 0;
                        $scope.$apply();
                    }
                }).fail(function(e) {
                    // something bad is happening!
                    console.log("error with delegates");
                    logAjaxError(e);
                });
            };

            $scope.openMenu = function(event){
                $scope.isDroppedDown = true;
                event.stopPropagation();
            };

            $scope.search = function() {
                if($scope.searchResultsCache[$scope.searchTerm] === undefined) {
                    if($scope.searchTerm === ''){
                        $scope.getDelegates();
                        $scope.searchResultsCache[$scope.searchTerm] = $scope.delegators;
                    }
                    else {
                        $.ajax({
                            url: getBaseUri() + '/delegators/search/' + encodeURIComponent($scope.searchTerm) + '?limit=10',
                            dataType: 'json',
                            success: function(data) {
                                $scope.delegators = data;
                                $scope.searchResultsCache[$scope.searchTerm] = $scope.delegators;
                                $scope.$apply();
                            }
                        }).fail(function() {
                            // something bad is happening!
                            console.log("error searching for delegates");
                        });
                    }
                } else {
                    $scope.delegators = $scope.searchResultsCache[$scope.searchTerm];
                }
            };

            $scope.switchUser = function(targetOrcid){
                $.ajax({
                    url: getBaseUri() + '/switch-user?username=' + targetOrcid,
                    dataType: 'json',
                    complete: function(data) {
                        window.location.reload();
                    }
                });
            };

            $document.bind(
                'click',
                function(event){
                    if(event.target.id !== "delegators-search"){
                        $scope.isDroppedDown = false;
                        $scope.searchTerm = '';
                        $scope.$apply();
                    }
                }
            );

            // init
            $scope.getDelegates();
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class SwitchUserCtrlNg2Module {}