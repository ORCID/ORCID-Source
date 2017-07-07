declare var $: any;
declare var colorbox: any;
declare var fixZindexIE7: any;
declare var isIE: any;
declare var orcidVar: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const EmailEditCtrl = angular.module('orcidApp').controller(
    'EmailEditCtrl', 
    [
        '$compile', 
        '$cookies', 
        '$scope', 
        '$timeout', 
        'bioBulkSrvc', 
        'emailSrvc', 
        'initialConfigService', 
        function EmailEditCtrl(
            $compile, 
            $cookies, 
            $scope, 
            $timeout, 
            bioBulkSrvc, 
            emailSrvc, 
            initialConfigService
        ) {

            var configuration = initialConfigService.getInitialConfiguration();

            bioBulkSrvc.initScope($scope);

            $scope.baseUri = orcidVar.baseUri;
            $scope.curPrivToggle = null;
            $scope.emailSrvc = emailSrvc;
            $scope.isPassConfReq = orcidVar.isPasswordConfirmationRequired;
            $scope.password = null;
            $scope.privacyHelp = {};
            $scope.scrollTop = 0;
            $scope.showConfirmationBox = false;
            $scope.showDeleteBox = false;
            $scope.showElement = {};
            $scope.showEmailVerifBox = false;
            $scope.showUnverifiedEmailSetPrimaryBox = false;
            $scope.verifyEmailObject;

            $scope.$on(
                'rebuildEmails', 
                function(event, data) {
                    $scope.emailSrvc.emails = data;
                }
            );

            $scope.$on(
                'unverifiedSetPrimary', 
                function(event, data){
                    if (data.newValue == true 
                        && configuration.showModalManualEditVerificationEnabled == true) {
                        $scope.showUnverifiedEmailSetPrimaryBox = true;
                    }
                    else {
                        $scope.showUnverifiedEmailSetPrimaryBox =false;
                    }
                    $scope.$apply(); 
                }
            );

            /* Workaround for dealing with the Email table styles in Asian languages */
            $scope.asianEmailTableStyleFix = function(){
                if ($cookies.get('locale_v3') == 'zh_CN' 
                    || $cookies.get('locale_v3') == 'zh_TW' 
                    || $cookies.get('locale_v3') == 'ja' 
                    || $cookies.get('locale_v3') == 'ko'){     
                    $scope.styleFix = false;
                    $scope.$watch(
                        function () {
                            return angular.element(document.getElementsByClassName("email-verified")[0]).length; 
                        },
                        function (newValue, oldValue) {
                            if(newValue > oldValue){
                                $scope.styleFix = true;                        
                            }        
                        }
                    );
                    
                 };
            };

            $scope.checkCredentials = function(popup) {
                $scope.password = null;
                if(orcidVar.isPasswordConfirmationRequired){
                    if (!popup){
                        $.colorbox({
                            html: $compile($('#check-password-modal').html())($scope)
                        });
                        $.colorbox.resize();
                    }else{
                        $scope.showConfirmationBox = true;            
                    }
                }else{
                    $scope.submitModal();
                }
            };

            $scope.closeDeleteBox = function(){
                $scope.showDeleteBox = false;
            };

            $scope.closeModal = function() {
                
                angular.element('#cboxLoadedContent').css({         
                    overflow: 'auto'
                });
                
                $.colorbox.close();
            };

            $scope.closeUnverifiedEmailSetPrimaryBox = function(){
                $scope.showUnverifiedEmailSetPrimaryBox = false;
            };

            $scope.closeVerificationBox = function(){
                $scope.showEmailVerifBox = false;
            };

            $scope.confirmDeleteEmail = function(email) {
                $scope.emailSrvc.delEmail = email;
                $.colorbox({
                    html : $compile($('#delete-email-modal').html())($scope)
                });
                $.colorbox.resize();
            };

            $scope.confirmDeleteEmailInline = function(email, $event) {
                $event.preventDefault();
                $scope.showDeleteBox = true;
                $scope.emailSrvc.delEmail = email;
                
                $scope.$watch(
                    function () {
                        return document.getElementsByClassName('delete-email-box').length; 
                    },
                    function (newValue, oldValue) {             
                        $.colorbox.resize();
                    }
                );
            };

            $scope.deleteEmail = function () {
                $scope.emailSrvc.deleteEmail(function() {
                    $scope.closeModal();
                });
            };

            $scope.deleteEmailInline = function () {
                $scope.emailSrvc.deleteEmail(function(){
                    $scope.showDeleteBox = false;            
                });
            };

            $scope.fixZindexesIE7 =  function(){
                fixZindexIE7('.popover',2000);
                fixZindexIE7('.popover-help-container',3000);
                fixZindexIE7('#privacy-bar',500);
                fixZindexIE7('.emailVisibility',5000);
                fixZindexIE7('.col-md-3', 6000);
                fixZindexIE7('.row', 7000);
            };

            $scope.getEmails = function() {
                $scope.emailSrvc.getEmails(function() {
                    if(isIE() == 7) $scope.fixZindexesIE7();
                });
            };

            $scope.hideTooltip = function(el){
                $scope.showElement[el] = false;
            };

            $scope.setBulkGroupPrivacy = function(priv) {
                for (var idx in $scope.emailSrvc.emails.emails) {
                    $scope.emailSrvc.emails.emails[idx].visibility = priv;
                }         
                $scope.emailSrvc.saveEmail();
            };

            $scope.setPrivacy = function(email, priv, $event) {
                $event.preventDefault();
                email.visibility = priv;
                $scope.curPrivToggle = null;
                $scope.emailSrvc.saveEmail();
            };

            $scope.showTooltip = function(el, event){       
                $scope.position = angular.element(event.target.parentNode).parent().position();
                angular.element('.edit-record-emails .popover-help-container').css({            
                    top: $scope.position.top + 33,
                    left: $scope.position.left
                });
                angular.element('#cboxLoadedContent').css({         
                    overflow: 'visible'
                });
                $scope.showElement[el] = true;
            };

            $scope.submitModal = function (obj, $event) {
                $scope.emailSrvc.inputEmail.password = $scope.password;
                $scope.emailSrvc.addEmail();
                if(!$scope.emailSrvc.popUp){
                    $.colorbox.close();    
                }
            };

            $scope.toggleClickPrivacyHelp = function(key) {
                if (document.documentElement.className.indexOf('no-touch') == -1) {
                    $scope.privacyHelp[key]=!$scope.privacyHelp[key];
                }
            };

            $scope.verifyEmail = function(email, popup) {
                
                $scope.verifyEmailObject = email;
                
                if( popup ){
                    $scope.emailSrvc.verifyEmail(email,function(data) {
                        $scope.showEmailVerifBox = true;
                        $scope.$apply();
                        $.colorbox.resize();
                   });    
                }else{
                    $scope.emailSrvc.verifyEmail(email,function(data) {
                        $.colorbox({
                            html : $compile($('#settings-verify-email-modal').html())($scope) 
                            //Name was changed to avoid conflicts with workspace verify email modal
                        });
                        $scope.$apply();
                        $.colorbox.resize();
                   });    
                }
                
            };

            //init            
            $scope.emailSrvc.getEmails();
            $scope.emailSrvc.initInputEmail();            
            $scope.asianEmailTableStyleFix(); 
            
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class EmailEditCtrlNg2Module {}