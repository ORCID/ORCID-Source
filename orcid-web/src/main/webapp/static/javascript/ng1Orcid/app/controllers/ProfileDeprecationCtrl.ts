declare var $: any;
declare var colorbox: any;
declare var getBaseUri: any;
declare var om: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const profileDeprecationCtrl = angular.module('orcidApp').controller(
    'profileDeprecationCtrl',
    [
        '$compile', 
        '$scope',
        function profileDeprecationCtrl(
            $compile,
            $scope
        ){
            $scope.deprecated_verified = false;
            $scope.deprecatedAccount = null;
            $scope.primary_verified = false;
            $scope.primaryAccount = null;
            $scope.showModal = false;

            $scope.cleanup = function(orcid_type){
                $("#deprecated_orcid").removeClass("orcid-red-background-input");
                $("#primary_orcid").removeClass("orcid-red-background-input");
                if(orcid_type == 'deprecated'){
                    if($scope.deprecated_verified == false){
                        $("#deprecated_orcid").addClass("error");
                    }
                    else{
                        $("#deprecated_orcid").removeClass("error");
                    }
                } else {
                    if($scope.primary_verified == false){
                        $("#primary_orcid").addClass("error");
                    }
                    else{
                        $("#primary_orcid").removeClass("error");
                    }
                }
            };

            $scope.closeModal = function() {
                $scope.deprecated_verified = false;
                $scope.primary_verified = false;
                $scope.deprecatedAccount = null;
                $scope.primaryAccount = null;
                $scope.showModal = false;       
                $.colorbox.close();
            };

            $scope.confirmDeprecateAccount = function(){
                var isOk = true;
                $scope.errors = null;
                if($scope.deprecated_verified === undefined 
                    || $scope.deprecated_verified == false)
                {
                    $("#deprecated_orcid").addClass("error orcid-red-background-input");
                    isOk = false;
                }

                if($scope.primary_verified === undefined 
                    || $scope.primary_verified == false)
                {
                    $("#primary_orcid").addClass("error orcid-red-background-input");
                    isOk = false;
                }

                if(isOk){
                    $.colorbox({
                        html : $compile($('#confirm-deprecation-modal').html())($scope),
                        scrolling: true,
                        onLoad: function() {
                            $('#cboxClose').remove();
                        }
                    });

                    $.colorbox.resize({width:"625px" , height:"400px"});
                }
            };

            $scope.deprecateAccount = function(){
                var deprecatedOrcid = $scope.deprecatedAccount.orcid;
                var primaryOrcid = $scope.primaryAccount.orcid;
                $.ajax({
                    url: getBaseUri()+'/admin-actions/deprecate-profile/deprecate-profile.json?deprecated=' + deprecatedOrcid + '&primary=' + primaryOrcid,
                    type: 'GET',
                    dataType: 'json',
                    success: function(data){
                        $scope.$apply(function(){
                            if(data.errors.length != 0){
                                $scope.errors = data.errors;
                            } else {
                                $scope.showSuccessModal(deprecatedOrcid, primaryOrcid);
                            }
                        });
                    }
                }).fail(function(error) {
                    // something bad is happening!
                    console.log("Error deprecating the account");
                });
            };

            $scope.findAccountDetails = function(orcid_type){
                var orcid;
                var orcidRegex = new RegExp("(\\d{4}-){3,}\\d{3}[\\dX]");
                
                if(orcid_type == 'deprecated') {
                    orcid = $scope.deprecatedAccount.orcid;
                } else {
                    orcid = $scope.primaryAccount.orcid;
                }

                // Reset styles
                $scope.cleanup(orcid_type);
                if(orcidRegex.test(orcid)){
                    $scope.getAccountDetails(orcid, function(data){
                        if(orcid_type == 'deprecated') {
                            $scope.invalid_regex_deprecated = false;
                            if(data.errors.length != 0){
                                $scope.deprecatedAccount.errors = data.errors;
                                $scope.deprecatedAccount.givenNames = null;
                                $scope.deprecatedAccount.familyName = null;
                                $scope.deprecatedAccount.primaryEmail = null;
                                $scope.deprecated_verified = false;
                            } else {
                                $scope.deprecatedAccount.errors = null;
                                $scope.deprecatedAccount.givenNames = data.givenNames;
                                $scope.deprecatedAccount.familyName = data.familyName;
                                $scope.deprecatedAccount.primaryEmail = data.email;
                                $scope.deprecated_verified = true;
                                $scope.cleanup(orcid_type);
                            }
                        } else {
                            $scope.invalid_regex_primary = false;
                            if(data.errors.length != 0){
                                $scope.primaryAccount.errors = data.errors;
                                $scope.primaryAccount.givenNames = null;
                                $scope.primaryAccount.familyName = null;
                                $scope.primaryAccount.primaryEmail = null;
                                $scope.primary_verified = false;
                            } else {
                                $scope.primaryAccount.errors = null;
                                $scope.primaryAccount.givenNames = data.givenNames;
                                $scope.primaryAccount.familyName = data.familyName;
                                $scope.primaryAccount.primaryEmail = data.email;
                                $scope.primary_verified = true;
                                $scope.cleanup(orcid_type);
                            }
                        }
                    });
                } else {
                    if(orcid_type == 'deprecated') {
                        if(!($scope.deprecatedAccount === undefined)){
                            $scope.invalid_regex_deprecated = true;
                            $scope.deprecatedAccount.errors = null;
                            $scope.deprecatedAccount.givenNames = null;
                            $scope.deprecatedAccount.familyName = null;
                            $scope.deprecatedAccount.primaryEmail = null;
                            $scope.deprecated_verified = false;
                        }
                    } else {
                        if(!($scope.primaryAccount === undefined)){
                            $scope.invalid_regex_primary = true;
                            $scope.primaryAccount.errors = null;
                            $scope.primaryAccount.givenNames = null;
                            $scope.primaryAccount.familyName = null;
                            $scope.primaryAccount.primaryEmail = null;
                            $scope.primary_verified = false;
                        }
                    }
                }
            };

            $scope.getAccountDetails = function (orcid, callback){
                $.ajax({
                    url: getBaseUri()+'/admin-actions/deprecate-profile/check-orcid.json?orcid=' + orcid,
                    type: 'GET',
                    dataType: 'json',
                    success: function(data){
                        callback(data);
                        $scope.$apply();
                    }
                }).fail(function(error) {
                    // something bad is happening!
                    console.log("Error getting account details for: " + orcid);
                });
            };

            $scope.showSuccessModal = function(deprecated, primary){
                $scope.successMessage = om.get('admin.profile_deprecation.deprecate_account.success_message').replace("{{0}}", deprecated).replace("{{1}}", primary);

                // Clean fields
                $scope.deprecated_verified = false;
                $scope.primary_verified = false;
                $scope.deprecatedAccount = null;
                $scope.primaryAccount = null;

                $.colorbox({
                    html : $compile($('#success-modal').html())($scope),
                    scrolling: true,
                    onLoad: function() {
                        $('#cboxClose').remove();
                    }
                });

                $.colorbox.resize({width:"450px" , height:"150px"});
            };

            $scope.toggleDeprecationModal = function(){
                $scope.showModal = !$scope.showModal;
                $('#deprecation_modal').toggle();
            };
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class profileDeprecationCtrlNg2Module {}