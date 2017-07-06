declare var ActSortState: any;
declare var GroupedActivities: any;
declare var orcidVar: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module

// Controller to show alert for unread notifications
export const PublicFundingCtrl = angular.module('orcidApp').controller(
    'PublicFundingCtrl',
    [
        '$compile', 
        '$filter', 
        '$scope', 
        'fundingSrvc', 
        'workspaceSrvc', 
        function (
            $compile, 
            $filter, 
            $scope, 
            fundingSrvc,
            workspaceSrvc 
        ){
            $scope.displayURLPopOver = {};
            $scope.editSources = {};
            $scope.fundingSrvc = fundingSrvc;
            $scope.moreInfo = {};
            $scope.showElement = {};
            $scope.sortState = new ActSortState(GroupedActivities.FUNDING);
            $scope.workspaceSrvc = workspaceSrvc;

            $scope.closeMoreInfo = function(key) {
                $scope.moreInfo[key] = false;
            };

            $scope.hideSources = function(group) {
                $scope.editSources[group.groupId] = false;
                group.activePutCode = group.defaultPutCode;
            };

            $scope.hideURLPopOver = function(id){
                $scope.displayURLPopOver[id] = false;
            };

            $scope.hideTooltip = function (key){        
                $scope.showElement[key] = false;
            };

            // remove once grouping is live
            $scope.moreInfoMouseEnter = function(key, $event) {
                $event.stopPropagation();
                if ( document.documentElement.className.indexOf('no-touch') >= -1 ) {
                    $scope.moreInfo[key]=true;
                }
            };

            $scope.renderTranslatedTitleInfo = function(funding) {
                var info = null;
                if(funding != null 
                    && funding.fundingTitle != null 
                    && funding.fundingTitle.translatedTitle != null) {
                    info = funding.fundingTitle.translatedTitle.content + ' - ' + funding.fundingTitle.translatedTitle.languageName;
                }
                return info;
            };

            $scope.showDetailsMouseClick = function(key, $event) {              
                $event.stopPropagation();
                $scope.moreInfo[key] = !$scope.moreInfo[key];
            };

            $scope.showSources = function(group) {
                $scope.editSources[group.groupId] = true;
            };

            $scope.showTooltip = function (key){
                $scope.showElement[key] = true;
            };

            $scope.showURLPopOver = function(id){
                $scope.displayURLPopOver[id] = true;
            };

            $scope.sort = function(key) {
                $scope.sortState.sortBy(key);
            };
            
            // remove once grouping is live
            $scope.toggleClickMoreInfo = function(key) {
                if ( document.documentElement.className.indexOf('no-touch') == -1 ) {
                    $scope.moreInfo[key]=!$scope.moreInfo[key];
                }
            };

            fundingSrvc.setIdsToAdd(orcidVar.fundingIdsJson);
            fundingSrvc.addFundingToScope(orcidVar.orcidId +'/fundings.json');
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class PublicFundingCtrlNg2Module {}