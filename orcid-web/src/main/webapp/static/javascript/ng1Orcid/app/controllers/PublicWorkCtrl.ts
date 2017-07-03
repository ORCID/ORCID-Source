declare var ActSortState: any;
declare var GroupedActivities: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const PublicWorkCtrl = angular.module('orcidApp').controller(
    'PublicWorkCtrl',
    [
        '$compile', 
        '$filter', 
        '$scope', 
        'workspaceSrvc', 
        'worksSrvc',
        function (
            $compile, 
            $filter, 
            $scope, 
            workspaceSrvc, 
            worksSrvc
        ) {
            $scope.badgesRequested = {};
            $scope.displayURLPopOver = {};
            $scope.editSources = {};
            $scope.moreInfoOpen = false;
            $scope.moreInfo = {};
            $scope.showBibtex = {};
            $scope.showElement = {};
            $scope.sortState = new ActSortState(GroupedActivities.ABBR_WORK);
            $scope.worksSrvc = worksSrvc;
            $scope.workspaceSrvc = workspaceSrvc;

            $scope.bibtexShowToggle = function (putCode) {
                $scope.showBibtex[putCode] = !($scope.showBibtex[putCode]);
            };

            $scope.closePopover = function(event) {
                $scope.moreInfoOpen = false;
                $('.work-more-info-container').css('display', 'none');
            };

            $scope.hideSources = function(group) {
                $scope.editSources[group.groupId] = false;
                group.activePutCode = group.defaultPutCode;
            };

            $scope.hideTooltip = function (element){        
                $scope.showElement[element] = false;
            };

            $scope.hideURLPopOver = function(id){
                $scope.displayURLPopOver[id] = false;
            };

            $scope.loadDetails = function(putCode, event) {
                // Close any open popover
                $scope.closePopover(event);
                $scope.moreInfoOpen = true;
                // Display the popover
                $(event.target).next().css('display','inline');
                $scope.worksSrvc.getGroupDetails(putCode, worksSrvc.constants.access_type.ANONYMOUS);
            };

            $scope.loadWorkInfo = function(putCode, event) {
                // Close any open popover
                $scope.closePopover(event);
                $scope.moreInfoOpen = true;
                // Display the popover
                $(event.target).next().css('display','inline');
                if($scope.worksSrvc.details[putCode] == null) {
                    $scope.worksSrvc.getGroupDetails(putCode, worksSrvc.constants.access_type.ANONYMOUS);
                } else {
                    $(event.target).next().css('display','inline');
                }
            };

            // remove once grouping is live
            $scope.moreInfoClick = function(work, $event) {
                if ( document.documentElement.className.indexOf('no-touch') == -1 ) {
                    $scope.moreInfoOpen?$scope.closePopover():$scope.loadWorkInfo(work.putCode.value, $event);
                }
            };

            // remove once grouping is live
            $scope.moreInfoMouseEnter = function(work, $event) {
                $event.stopPropagation();
                if (document.documentElement.className.indexOf('no-touch') >= 0 ){
                    $scope.loadWorkInfo(work.putCode.value, $event);
                }
                else {
                    $scope.moreInfoOpen?$scope.closePopover():$scope.loadWorkInfo(work.putCode.value, $event);
                }
            };

            $scope.renderTranslatedTitleInfo = function(putCode) {
                var info = null;

                if(putCode != null 
                    && $scope.worksSrvc.details[putCode] != null 
                    && $scope.worksSrvc.details[putCode].translatedTitle != null
                ) {
                    info = $scope.worksSrvc.details[putCode].translatedTitle.content + ' - ' + $scope.worksSrvc.details[putCode].translatedTitle.languageName;
                }

                return info;
            };

            $scope.showDetailsMouseClick = function(group, $event) {
                $event.stopPropagation();
                $scope.moreInfo[group.groupId] = !$scope.moreInfo[group.groupId];
                for (var idx in group.activities) {
                    $scope.loadDetails(group.activities[idx].putCode.value, $event);
                }
            };

            $scope.showMozillaBadges = function(putCode){
                $scope.$watch(
                    function () { 
                        return document.getElementsByClassName('badge-container-' + putCode).length; 
                    },
                    function (newValue, oldValue) {
                        var c = null;
                        var code = null;
                        var dois = null;
                        var s = null;
                        if (newValue !== oldValue) {
                            if ($scope.badgesRequested[putCode] == null){
                                dois = worksSrvc.getUniqueDois(putCode);
                                c = document.getElementsByClassName('badge-container-' + putCode);
                                for (var i = 0; i <= dois.length - 1; i++){
                                    code = 'var conf={"article-doi": "' + dois[i] + '", "container-class": "badge-container-' + putCode + '"};showBadges(conf);';
                                    s = document.createElement('script');
                                    s.type = 'text/javascript';
                                    try {
                                        s.appendChild(document.createTextNode(code));
                                        c[0].appendChild(s);
                                    } catch (e) {
                                        s.text = code;
                                        c[0].appendChild(s);
                                    }
                                }
                                $scope.badgesRequested[putCode] = true;
                            }
                        }
                    }
                );  
            };

            $scope.showSources = function(group) {
                $scope.editSources[group.groupId] = true;
            };

            $scope.showTooltip = function (element){        
                $scope.showElement[element] = true;
            };

            $scope.showURLPopOver = function(id){
                $scope.displayURLPopOver[id] = true;
            };

            $scope.sort = function(key) {
                $scope.sortState.sortBy(key);
            };
            
            $scope.worksSrvc.loadAbbrWorks(worksSrvc.constants.access_type.ANONYMOUS);
            


            

            

            

            

            

            

            

            

            

            
            
            
            
            
            
            
            
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class PublicWorkCtrlNg2Module {}