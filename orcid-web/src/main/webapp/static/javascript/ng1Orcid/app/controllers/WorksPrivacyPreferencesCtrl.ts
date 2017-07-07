import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const WorksPrivacyPreferencesCtrl = angular.module('orcidApp').controller(
    'WorksPrivacyPreferencesCtrl',
    [
        '$scope', 
        'commonSrvc', 
        'prefsSrvc', 
        function (
            $scope, 
            commonSrvc,
            prefsSrvc
        ) {
            $scope.commonSrvc = commonSrvc;
            $scope.prefsSrvc = prefsSrvc;
            $scope.privacyHelp = {};
            $scope.showElement = {};

            $scope.hideTooltip = function(el){
                $scope.showElement[el] = false;
            };

            $scope.showTooltip = function(el){
                $scope.showElement[el] = true;
            };

            $scope.toggleClickPrivacyHelp = function(key) {
                if (document.documentElement.className.indexOf('no-touch')  == -1 ) {
                    $scope.privacyHelp[key]=!$scope.privacyHelp[key];
                }
            };

            $scope.updateActivitiesVisibilityDefault = function(priv, $event) {
                $scope.prefsSrvc.prefs['default_visibility'] = priv;        
                $scope.prefsSrvc.updateDefaultVisibility();        
            };
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class WorksPrivacyPreferencesCtrlNg2Module {}