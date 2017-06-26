import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const ConsortiaListController = angular.module('orcidApp').controller(
    'ConsortiaListController',
    [
        '$scope', 
        '$sce', 
        'clearMemberListFilterSrvc', 
        'membersListSrvc', 
        function (
            $scope, 
            $sce, 
            clearMemberListFilterSrvc,
            membersListSrvc
        ){
            var alphaStr = "abcdefghijklmnopqrstuvwxyz";

            $scope.membersListSrvc = membersListSrvc;
            $scope.displayMoreDetails = {};
            
            //clear filters
            $scope.clearFilters = function(){
                return clearMemberListFilterSrvc.clearFilters($scope);
            }

            //render html from salesforce data
            $scope.renderHtml = function (htmlCode) {
                return $sce.trustAsHtml(htmlCode);
            };

            $scope.toggleDisplayMoreDetails = function(memberId, consortiumLeadId){
                membersListSrvc.getDetails(memberId, consortiumLeadId);
                $scope.displayMoreDetails[memberId] = !$scope.displayMoreDetails[memberId];
            }
            
            //create alphabetical list for filter
            $scope.alphabet = alphaStr.toUpperCase().split("");
            $scope.activeLetter = '';
            
            $scope.activateLetter = function(letter) {
                $scope.activeLetter = letter
            };
                            
            // populate the consortia feed
            $scope.membersListSrvc.getConsortiaList();    
            
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class ConsortiaListControllerNg2Module {}