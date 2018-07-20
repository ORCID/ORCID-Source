declare var ActSortState: any;
declare var GroupedActivities: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const PublicPeerReviewCtrl = angular.module('orcidApp').controller(
    'PublicPeerReviewCtrl',
    [
        '$compile', 
        '$filter', 
        '$scope', 
        'peerReviewSrvc',
        'workspaceSrvc', 
        function (
            $compile, 
            $filter, 
            $scope, 
            peerReviewSrvc,
            workspaceSrvc
        ) {
            $scope.peerReviewSrvc = peerReviewSrvc;
            $scope.workspaceSrvc  = workspaceSrvc;
            $scope.showDetails = {};
            $scope.showElement = {};
            $scope.showPeerReviewDetails = new Array();
            $scope.sortHideOption = true;
            $scope.sortState = new ActSortState(GroupedActivities.PEER_REVIEW);
             
            $scope.sort = function(key) {
                $scope.sortState.sortBy(key);
                peerReviewSrvc.getPublicPeerReviews(!$scope.sortState.reverseKey[key]);       
            };
             
            $scope.showDetailsMouseClick = function(groupId, $event){
                $event.stopPropagation();
                $scope.showDetails[groupId] = !$scope.showDetails[groupId];
            };
            
            $scope.showTooltip = function (element){        
                $scope.showElement[element] = true;
            };

            $scope.hideTooltip = function (element){        
                $scope.showElement[element] = false;
            };
            
            
            $scope.showMoreDetails = function(putCode){  
                $scope.showPeerReviewDetails.length = 0;
                $scope.showPeerReviewDetails[putCode] = true;   
            };
            
            $scope.hideMoreDetails = function(putCode){
                $scope.showPeerReviewDetails.length = 0;
                $scope.showPeerReviewDetails[putCode] = false;
            };
            
            // Init
            $scope.peerReviewSrvc.getPublicPeerReviews(true);       
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class PublicPeerReviewCtrlNg2Module {}