declare var getBaseUri: any;
declare var logAjaxError: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const RequestResendClaimCtrl = angular.module('orcidApp').controller(
    'RequestResendClaimCtrl', 
    [
        '$compile', 
        '$scope', 
        function RequestResendClaimCtrl(
            $compile,
            $scope
        ) {

            var getParameterByName = function(name) {
                var url = window.location.href;
                var name = name.replace(/[\[\]]/g, "\\$&");
                var 
                    regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
                    results = regex.exec(url)
                ;
                if (!results){
                    return null;
                } 
                if (!results[2]){
                    return '';
                } 
                return decodeURIComponent(results[2].replace(/\+/g, " "));
            };
    
            $scope.getRequestResendClaim = function() {
                $.ajax({
                    url: getBaseUri() + '/resend-claim.json',
                    dataType: 'json',
                    success: function(data) {
                        $scope.requestResendClaim = data;
                        $scope.requestResendClaim.email = getParameterByName("email");
                        $scope.$apply();
                    }
                }).fail(function(e) {
                    console.log("error getting resend-claim.json");
                    logAjaxError(e);
                });  
            };

            $scope.postResendClaimRequest = function() {
                $.ajax({
                    url: getBaseUri() + '/resend-claim.json',
                    dataType: 'json',
                    type: 'POST',
                    data:  angular.toJson($scope.requestResendClaim),
                    contentType: 'application/json;charset=UTF-8',
                    success: function(data) {
                        $scope.requestResendClaim = data;
                        $scope.requestResendClaim.email = "";
                        $scope.$apply();
                    }
                }).fail(function(){
                    console.log("error posting to /resend-claim.json");
                });  
            };
            
            $scope.validateRequestResendClaim = function() {
                $.ajax({
                    url: getBaseUri() + '/validate-resend-claim.json',
                    dataType: 'json',
                    type: 'POST',
                    data:  angular.toJson($scope.requestResendClaim),
                    contentType: 'application/json;charset=UTF-8',
                    success: function(data) {
                        $scope.requestResendClaim = data;
                        $scope.requestResendClaim.successMessage = null;
                        $scope.$apply();
                    }
                }).fail(function() {
                    console.log("error validating validate-resend-claim.json");
                });  
            };
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class RequestResendClaimCtrlNg2Module {}