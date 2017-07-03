declare var getBaseUri: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const RecordCorrectionsCtrl = angular.module('orcidApp').controller(
    'RecordCorrectionsCtrl', 
    [
        '$compile', 
        '$scope', 
        'utilsService', 
        function RecordCorrectionsCtrl(
            $compile, 
            $scope, 
            utilsService
        ) {
            $scope.currentPage = null;
            $scope.currentElement = null;
            
            $scope.getNextPage = function() {
            	var nextPageUrl = getBaseUri() + '/record-corrections/next';
            	if($scope.currentPage != null) {
            		nextPageUrl += '/' + $scope.currentPage.lastElementId
            	}    	
                $.ajax({
                    url: nextPageUrl,
                    dataType: 'json',
                    success: function(data) {
                    	$scope.currentPage = data;
                        $scope.$apply();
                    }
                }).fail(function(){
                    // something bad is happening!
                    console.log("error fetching next page");
                });
            };

            $scope.getPreviousPage = function() {
            	var previousPageUrl = getBaseUri() + '/record-corrections/previous';
            	if($scope.currentPage != null) {
            		previousPageUrl += '/' + $scope.currentPage.firstElementId
            	}
                $.ajax({
                    url: previousPageUrl,            
                    dataType: 'json',
                    success: function(data) {            	
                    	$scope.currentPage = data;
                        $scope.$apply();
                    }
                }).fail(function() {
                    // something bad is happening!
                	console.log("error fetching previous page");
                });
            };    

            $scope.getNextPage();        
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class RecordCorrectionsCtrlNg2Module {}