declare var $: any;
declare var colorbox: any;
declare var getBaseUri: any;
declare var orcidVar: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const SocialCtrl = angular.module('orcidApp').controller(
    'SocialCtrl',
    [
        '$compile', 
        '$scope', 
        'discoSrvc', 
        function SocialCtrl(
            $compile, 
            $scope, 
            discoSrvc
        ){
            $scope.isPasswordConfirmationRequired = orcidVar.isPasswordConfirmationRequired;
            $scope.showLoader = false;
            
            $scope.$watch(function() { return discoSrvc.feed; }, function(){
                $scope.populateIdPNames();
            });

            $scope.changeSorting = function(column) {
                var sort = $scope.sort;
                if (sort.column === column) {
                    sort.descending = !sort.descending;
                } else {
                    sort.column = column;
                    sort.descending = false;
                }
            };

            $scope.closeModal = function() {
                $.colorbox.close();
            };

            $scope.confirmRevoke = function(socialAccount) {
                $scope.errors = [];
                $scope.socialAccount = socialAccount;
                $scope.idToManage = socialAccount.id;
                $.colorbox({
                    html : $compile($('#revoke-social-account-modal').html())($scope),            
                    onComplete: function() {
                        $.colorbox.resize({height:"200px", width:"500px"});        
                    }
                });
            };

            $scope.getSocialAccounts = function() {
                $.ajax({
                    url: getBaseUri() + '/account/socialAccounts.json',
                    dataType: 'json',
                    success: function(data) {
                        $scope.socialAccounts = data;
                        $scope.populateIdPNames();
                        $scope.$apply();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("error getting social accounts");
                });
            };

            $scope.populateIdPNames = function() {
                var account = null;
                var name = null;
                if(discoSrvc.feed != null) {
                    for(var i in $scope.socialAccounts){
                        account = $scope.socialAccounts[i];
                        name = discoSrvc.getIdPName(account.id.providerid);
                        account.idpName = name;
                    }
                }
            };

            $scope.revoke = function () {
                var revokeSocialAccount = {
                    idToManage: null,
                    password: null
                };
                revokeSocialAccount.idToManage = $scope.idToManage;
                revokeSocialAccount.password = $scope.password;
                $.ajax({
                    url: getBaseUri() + '/account/revokeSocialAccount.json',
                    type: 'POST',
                    data:  angular.toJson(revokeSocialAccount),
                    contentType: 'application/json;charset=UTF-8',
                    success: function(data) {
                        if(data.errors.length === 0){
                            $scope.getSocialAccounts();
                            $scope.$apply();
                            $scope.closeModal();
                            $scope.password = "";
                        }
                        else{
                            $scope.errors = data.errors;
                            $scope.$apply();
                        }
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("$SocialCtrl.revoke() error");
                });
            };

            $scope.sort = {
                column: 'providerUserId',
                descending: false
            };

            // init
            $scope.getSocialAccounts();

        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class SocialCtrlNg2Module {}