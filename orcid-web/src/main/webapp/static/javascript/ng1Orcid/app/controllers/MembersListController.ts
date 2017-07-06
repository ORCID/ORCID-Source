import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module

export const MembersListController = angular.module('orcidApp').controller(
    'MembersListController',[
        '$sce', 
        '$scope', 
        'clearMemberListFilterSrvc', 
        'membersListSrvc', 
        function (
            $sce, 
            $scope, 
            clearMemberListFilterSrvc,
            membersListSrvc
        ){
            var alphaStr = "abcdefghijklmnopqrstuvwxyz";

            $scope.activeLetter = '';
            $scope.alphabet = alphaStr.toUpperCase().split("");
            $scope.displayMoreDetails = {};
            $scope.membersListSrvc = membersListSrvc;
            
            $scope.activateLetter = function(letter) {
              $scope.activeLetter = letter
            };

            //clear filters 
            $scope.clearFilters = function(){
                return clearMemberListFilterSrvc.clearFilters($scope);
            }

            //render html from salesforce data
            $scope.renderHtml = function (htmlCode) {
                return $sce.trustAsHtml(htmlCode);
            };

            $scope.toggleDisplayMoreDetails = function(memberId, consortiumLeadId){
                $scope.membersListSrvc.getDetails(memberId, consortiumLeadId);
                $scope.displayMoreDetails[memberId] = !$scope.displayMoreDetails[memberId];
            }
            
            //create alphabetical list for filter
                
            // populate the members feed
            $scope.membersListSrvc.getMembersList();
            
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class MembersListControllerNg2Module {}