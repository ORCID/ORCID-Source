declare var $: any;
declare var colorbox: any;
declare var getBaseUri: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const profileReviewCtrl = angular.module('orcidApp').controller(
    'profileReviewCtrl', 
    [
        '$compile', 
        '$scope', 
        function(
            $compile,
            $scope
        ){
            $scope.orcidToReview = '';
            $scope.orcidToUnreview = '';
            $scope.showReviewModal = false;
            $scope.showUnreviewModal = false;
            
            $scope.closeModal = function() {        
                $.colorbox.close();
            };

            $scope.reviewAccount = function() {
                $.ajax({
                    url: getBaseUri()+'/admin-actions/review-accounts.json',
                    type: 'POST',
                    data: $scope.orcidToReview,
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data){   
                        $scope.result = data;               
                        $scope.orcidToReview = '';
                        $scope.$apply();
                    }
                }).fail(function(error) {
                    // something bad is happening!
                    console.log("Error while reviewing account");
                });
            };

            $scope.toggleReviewModal = function(){
                $scope.showReviewModal = !$scope.showReviewModal;
                $('#review_modal').toggle();
            };
            
            $scope.toggleUnreviewModal = function(){
                $scope.showUnreviewModal = !$scope.showUnreviewModal;
                $('#unreview_modal').toggle();
            };
            
            $scope.unreviewAccount = function() {
                $.ajax({
                    url: getBaseUri()+'/admin-actions/unreview-accounts.json',
                    type: 'POST',
                    data: $scope.orcidToUnreview,
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data){   
                        $scope.result = data;               
                        $scope.orcidToUnreview = '';
                        $scope.$apply();
                    }
                }).fail(function(error) {
                    // something bad is happening!
                    console.log("Error while unlocking account");
                });
            };
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class profileReviewCtrlNg2Module {}