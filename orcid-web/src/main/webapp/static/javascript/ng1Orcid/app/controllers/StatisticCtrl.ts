declare var getBaseUri: any;
declare var logAjaxError: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const statisticCtrl = angular.module('orcidApp').controller(
    'statisticCtrl',
    [
        '$scope', 
        function (
            $scope
        ){
            $scope.liveIds = 0;
            
            $scope.getLiveIds = function(){
                $.ajax({
                    url: getBaseUri()+'/statistics/liveids.json',
                    type: 'GET',
                    dataType: 'html',
                    success: function(data){
                        $scope.liveIds = data;
                        $scope.$apply($scope.liveIds);
                    }
                }).fail(function(e) {
                    // something bad is happening!
                    console.log("Error getting statistics Live iDs total amount");
                    logAjaxError(e);
                });
            };

            $scope.getLiveIds();
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class statisticCtrlNg2Module {}