declare var getWindowWidth: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module

export const headerCtrl = angular.module('orcidApp').controller(
    'headerCtrl',
    [
        '$scope', 
        '$window', 
        function (
            $scope, 
            $window
        ){ 
            $scope.conditionsActive = false;
            $scope.filterActive = false;
            $scope.menuVisible = false;
            $scope.searchFilterChanged = false;
            $scope.searchVisible = false;
            $scope.secondaryMenuVisible = {};
            $scope.settingsVisible = false;
            $scope.tertiaryMenuVisible = {};
            
            $scope.filterChange = function(){
                $scope.searchFilterChanged = true;
            };

            $scope.handleMobileMenuOption = function($event){
                var w = getWindowWidth();           
                
                $event.preventDefault();
                
                if( w > 767) {               
                    window.location.href = $event.target.getAttribute('href');
                }
            };

            $scope.hideSearchFilter = function(){
                var inputValue = document.getElementById('input1') as HTMLInputElement;
                var searchInputValue = inputValue.value;
                if (searchInputValue === ""){
                    setTimeout(function() {
                        if ($scope.searchFilterChanged === false) {
                            $scope.filterActive = false;
                        }
                    }, 3000);
                }
            };

            $scope.searchBlur = function(){     
                $scope.hideSearchFilter();
                $scope.conditionsActive = false;        
            };

            $scope.searchFocus = function(){
                $scope.filterActive = true;
                $scope.conditionsActive = true;
            };
            
            $scope.toggleMenu = function(){
                $scope.menuVisible = !$scope.menuVisible;
                $scope.searchVisible = false;
                $scope.settingsVisible = false;     
            };
            
            $scope.toggleSearch = function(){
                $scope.searchVisible = !$scope.searchVisible;
                $scope.menuVisible = false;     
                $scope.settingsVisible = false;
            };

            $scope.toggleSecondaryMenu = function(submenu){
                $scope.secondaryMenuVisible[submenu] = !$scope.secondaryMenuVisible[submenu];
            };

            $scope.toggleSettings = function(){
                $scope.settingsVisible = !$scope.settingsVisible;
                $scope.menuVisible = false;
                $scope.searchVisible = false;
            };
            
            $scope.toggleTertiaryMenu = function(submenu){
                $scope.tertiaryMenuVisible[submenu] = !$scope.tertiaryMenuVisible[submenu];
            };
            
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class headerCtrlNg2Module {}