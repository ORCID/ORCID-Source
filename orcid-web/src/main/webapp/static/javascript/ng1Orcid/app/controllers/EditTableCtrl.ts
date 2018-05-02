//migrated

declare var fixZindexIE7: any;
declare var om: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const EditTableCtrl = angular.module('orcidApp').controller(
    'EditTableCtrl', 
    [
        '$scope', 
        function ($scope) {

            // deactivate edit row
            $scope.deactivateUpdateToggleText = function () {
                if ($scope.showEditDeactivate) {
                    $scope.deactivateToggleText = om.get("manage.editTable.hide");
                }
                else {
                    $scope.deactivateToggleText = om.get("manage.editTable.deactivateRecord");
                }
            };

            $scope.deprecateUpdateToggleText = function () {
                if ($scope.showEditDeprecate) {
                    $scope.deprecateToggleText = om.get("manage.editTable.hide");
                }
                else {
                    $scope.deprecateToggleText = om.get("manage.editTable.removeDuplicate");
                } 
            };

            // email preferences edit row
            $scope.emailPreferencesUpdateToggleText = function () {
                if ($scope.showEditEmailPreferences) {
                    $scope.emailPreferencesToggleText = om.get("manage.editTable.hide");
                }
                else {
                    $scope.emailPreferencesToggleText = om.get("manage.editTable.edit");
                } 
            };

            // email edit row
            $scope.emailUpdateToggleText = function () {
                if ($scope.showEditEmail) {
                    $scope.emailToggleText = om.get("manage.editTable.hide");
                }
                else {
                    $scope.emailToggleText = om.get("manage.editTable.edit");
                } 
            };

            $scope.getMyDataUpdateToggleText = function () {
                if($scope.showEditGetMyData){
                    $scope.getMyDataToggleText = om.get("manage.editTable.hide");
                } else {
                    $scope.getMyDataToggleText = om.get("manage.editTable.show");
                }  
            }; 

            $scope.fixIE7zIndexes = function() {
                fixZindexIE7('tr', 999999);
                fixZindexIE7('#privacy-settings', 5000);
            };

            $scope.languageUpdateToggleText = function () {
                if ($scope.showEditLanguage) {
                    $scope.languageToggleText = om.get("manage.editTable.hide");
                }
                else {
                    $scope.languageToggleText = om.get("manage.editTable.edit");
                } 
            };

            $scope.openEmailEdit = function() {
                $scope.showEditEmail = true;
                $scope.emailUpdateToggleText();
                window.location.hash = "#editEmail"
            };

            // password edit row
            $scope.passwordUpdateToggleText = function () {
                if ($scope.showEditPassword) {
                    $scope.passwordToggleText = om.get("manage.editTable.hide");
                }
                else {
                    $scope.passwordToggleText = om.get("manage.editTable.edit");
                } 
            };

            // privacy preferences edit row
            $scope.privacyPreferencesUpdateToggleText = function () {
                if ($scope.showEditPrivacyPreferences) {
                    $scope.privacyPreferencesToggleText = om.get("manage.editTable.hide");
                }
                else {
                    $scope.privacyPreferencesToggleText = om.get("manage.editTable.edit");
                }
            };

            // security question edit row
            $scope.securityQuestionUpdateToggleText = function () {
                if ($scope.showEditSecurityQuestion) {
                    $scope.securityQuestionToggleText = om.get("manage.editTable.hide");
                }
                else {
                    $scope.securityQuestionToggleText = om.get("manage.editTable.edit");
                } 
            };

            /* Social Networks */
            $scope.socialNetworksUpdateToggleText = function () {
                if ($scope.showEditSocialSettings) {
                    $scope.socialNetworksToggleText = om.get("manage.socialNetworks.hide");
                }
                else {
                    $scope.socialNetworksToggleText = om.get("manage.socialNetworks.edit");
                }
            };

            $scope.toggle2FAEdit = function() {
                $scope.showEdit2FA = !$scope.showEdit2FA;
                $scope.update2FAToggleText();
            };
            
            $scope.toggleGetMyDataEdit = function() {                
                $scope.showEditGetMyData = !$scope.showEditGetMyData;
                $scope.getMyDataUpdateToggleText();
            };

            $scope.toggleDeactivateEdit = function() {
                $scope.showEditDeactivate = !$scope.showEditDeactivate;
                $scope.deactivateUpdateToggleText();
            };

            $scope.toggleDeprecateEdit = function() {
                $scope.showEditDeprecate = !$scope.showEditDeprecate;
                $scope.deprecateUpdateToggleText();
            };

            $scope.toggleEmailEdit = function() {
                $scope.showEditEmail = !$scope.showEditEmail;
                $scope.emailUpdateToggleText();
            };

            $scope.toggleEmailPreferencesEdit = function() {
                $scope.showEditEmailPreferences = !$scope.showEditEmailPreferences;
                $scope.emailPreferencesUpdateToggleText();
            };

            /* Language preferences */
            $scope.toggleLanguageEdit = function() {
                $scope.showEditLanguage = !$scope.showEditLanguage;
                $scope.languageUpdateToggleText();
            };

            $scope.togglePasswordEdit = function() {
                $scope.showEditPassword = !$scope.showEditPassword;
                $scope.passwordUpdateToggleText();
            };

            $scope.togglePrivacyPreferencesEdit = function() {
                $scope.showEditPrivacyPreferences = !$scope.showEditPrivacyPreferences;
                $scope.privacyPreferencesUpdateToggleText();
            };

            $scope.toggleSecurityQuestionEdit = function() {
                $scope.showEditSecurityQuestion = !$scope.showEditSecurityQuestion;
                $scope.securityQuestionUpdateToggleText();
            };

            $scope.toggleSocialNetworksEdit = function(){
                $scope.showEditSocialSettings = !$scope.showEditSocialSettings;
                $scope.socialNetworksUpdateToggleText();
            }; 

            $scope.update2FAToggleText = function () {
                if ($scope.showEdit2FA) {
                    $scope.twoFAToggleText = om.get("manage.editTable.hide");
                }
                else {
                    $scope.twoFAToggleText = om.get("manage.editTable.edit");
                } 
            };
            
            $scope.languageUpdateToggleText();
            
            // init email edit row
            $scope.showEditEmail = (window.location.hash === "#editEmail");
            $scope.emailUpdateToggleText();
            
            // init password row
            $scope.showEditPassword = (window.location.hash === "#editPassword");
            $scope.passwordUpdateToggleText();
               
            // init deactivate and Z-Indexes Fix
            $scope.showEditDeactivate = (window.location.hash === "#editDeactivate");
            $scope.deactivateUpdateToggleText();
            $scope.fixIE7zIndexes();
            
            $scope.showEditDeprecate = (window.location.hash === "#editDeprecate");
            $scope.deprecateUpdateToggleText();
            
            $scope.showEdit2FA = (window.location.hash === "#edit2FA");
            $scope.update2FAToggleText();

            // init privacy preferences
            $scope.showEditPrivacyPreferences = (window.location.hash === "#editPrivacyPreferences");
            $scope.privacyPreferencesUpdateToggleText();
            
            // init email preferences
            $scope.showEditEmailPreferences = (window.location.hash === "#editEmailPreferences");
            $scope.emailPreferencesUpdateToggleText();
            
            // init security question
            $scope.showEditSecurityQuestion = (window.location.hash === "#editSecurityQuestion");
            $scope.securityQuestionUpdateToggleText();
            
            // init social networks row
            $scope.showEditSocialSettings = (window.location.hash === "#editSocialNetworks");
            $scope.socialNetworksUpdateToggleText();

            // init get my data row
            $scope.getMyDataUpdateToggleText();          
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class EditTableCtrlNg2Module {}