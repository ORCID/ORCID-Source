declare var $: any;
declare var colorbox: any;
declare var getBaseUri: any;
declare var formatDate: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const revokeApplicationFormCtrl = angular.module('orcidApp').controller(
    'revokeApplicationFormCtrl',
    [
        '$compile', 
        '$scope', 
        function (
            $compile,
            $scope
        ){
            $scope.applicationSummary = null;
            $scope.applicationSummaryList = null;
            
            $scope.closeModal = function() {
                $.colorbox.close();
            };

            $scope.confirmRevoke = function(applicationSummary){
                $scope.applicationSummary = applicationSummary;
                $.colorbox({
                    html : $compile($('#confirm-revoke-access-modal').html())($scope),
                    transition: 'fade',
                    close: '',
                    onLoad: function() {
                        $('#cboxClose').remove();
                    },
                    onComplete: function() {$.colorbox.resize();},
                    scrolling: true
                });
            };

            $scope.getApplications = function() {
                $.ajax({
                    url: getBaseUri()+'/account/get-trusted-orgs.json',
                    type: 'GET',
                    dataType: 'json',
                    success: function(data){                
                        $scope.$apply(function(){
                            for(var index1 = 0; index1 < data.length; index1 ++) {
                                data[index1].approvalDate = formatDate(data[index1].approvalDate);                      
                            }
                            $scope.applicationSummaryList = data;                   
                            
                        });
                    }
                }).fail(function(error) {
                    // something bad is happening!
                    console.log("Error finding the information");
                });
            };

            $scope.getApplicationUrlLink = function(application) {
                if(application.websiteValue != null) {
                    if(application.websiteValue.lastIndexOf('http://') === -1 
                        && application.websiteValue.lastIndexOf('https://') === -1) {
                        return '//' + application.websiteValue;
                    } else {
                        return application.websiteValue;
                    }
                }
                return '';
            };

            $scope.revokeAccess = function(){
                $.ajax({
                    url: getBaseUri() + '/account/revoke-application.json?tokenId='+ $scope.applicationSummary.tokenId,
                    type: 'POST',
                    success: function(data) {
                        $scope.getApplications();
                        $scope.closeModal();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("revokeApplicationFormCtrl.revoke() error");
                });
            };

            $scope.getApplications();
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class revokeApplicationFormCtrlNg2Module {}