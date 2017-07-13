declare var $: any;
declare var colorbox: any;
declare var getBaseUri: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const VerifyEmailCtrl = angular.module('orcidApp').controller(
    'VerifyEmailCtrl', 
    [
        '$compile', 
        '$scope', 
        'emailSrvc', 
        'initialConfigService', 
        function (
            $compile, 
            $scope, 
            emailSrvc, 
            initialConfigService
        ) {

            $scope.emailSent = false;
            $scope.loading = true;

            $scope.closeColorBox = function() {
                $.ajax({
                    url: getBaseUri() + '/account/delayVerifyEmail.json',
                    type: 'get',
                    contentType: 'application/json;charset=UTF-8',
                    success: function(data) {
                        // alert( "Verification Email Send To: " +
                        // $scope.emailsPojo.emails[idx].value);
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("error with multi email");
                });
                $.colorbox.close();
            };

            $scope.getEmails = function() {
                $.ajax({
                    url: getBaseUri() + '/account/emails.json',
                    // type: 'POST',
                    // data: $scope.emailsPojo,
                    dataType: 'json',
                    success: function(data) {
                        var configuration = initialConfigService.getInitialConfiguration();
                        var colorboxHtml = null;
                        var primeVerified = false;

                        $scope.verifiedModalEnabled = configuration.showModalManualEditVerificationEnabled;
                        $scope.emailsPojo = data;
                        $scope.$apply();

                        for (var i in $scope.emailsPojo.emails) {
                            if ($scope.emailsPojo.emails[i].primary  == true) {
                                $scope.primaryEmail = $scope.emailsPojo.emails[i].value;
                                if ($scope.emailsPojo.emails[i].verified) {
                                    primeVerified = true;
                                }
                            };
                        };

                        if ( primeVerified == false 
                            && getBaseUri().indexOf("sandbox") == -1 
                        ) {
                            colorboxHtml = $compile($('#verify-email-modal').html())($scope);
                            $scope.$apply();
                            $.colorbox({
                                html : colorboxHtml,
                                escKey:false,
                                overlayClose:false,
                                transition: 'fade',
                                close: '',
                                scrolling: false
                            });
                            $.colorbox.resize({width:"500px"});
                        };
                        $scope.loading = false;
                        $scope.$apply();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("error with multi email");
                });
            };

            $scope.verifyEmail = function() {
                var colorboxHtml = null;
                $.ajax({
                    url: getBaseUri() + '/account/verifyEmail.json',
                    type: 'get',
                    data:  { "email": $scope.primaryEmail },
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        // alert( "Verification Email Send To: " +
                        // $scope.emailsPojo.emails[idx].value);
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("error with multi email");
                });
                
                colorboxHtml = $compile($('#verify-email-modal-sent').html())($scope);

                $scope.emailSent = true;
                $.colorbox({
                    html : colorboxHtml,
                    escKey: true,
                    overlayClose: true,
                    transition: 'fade',
                    close: '',
                    scrolling: false
                });
                $.colorbox.resize({height:"200px", width:"500px"});
            };

            $scope.getEmails();
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class VerifyEmailCtrlNg2Module {}