declare var $: any;
declare var colorbox: any;
declare var getBaseUri: any;
declare var orcidGA: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const ClaimThanks = angular.module('orcidApp').controller(
    'ClaimThanks', 
    [
        '$scope', 
        '$compile', 
        function (
            $scope, 
            $compile
        ) {
            $scope.close = function () {
                $.colorbox.close();
            };

            $scope.getSourceGrantReadWizard = function(){
                $.ajax({
                    url: getBaseUri() + '/my-orcid/sourceGrantReadWizard.json',
                    dataType: 'json',
                    success: function(data) {
                        $scope.sourceGrantReadWizard = data;
                        $scope.$apply();
                        $scope.showThanks();
                    }
                }).fail(function(){
                    // something bad is happening!
                    console.log("error fetching external identifiers");
                });

            };

            $scope.showThanks = function () {
                var colorboxHtml;
                if ($scope.sourceGrantReadWizard.url == null) {
                    colorboxHtml = $compile($('#claimed-record-thanks').html())($scope);
                }
                else {
                    colorboxHtml = $compile($('#claimed-record-thanks-source-grand-read').html())($scope);
                }
                $.colorbox({
                    html : colorboxHtml,
                    escKey: true,
                    overlayClose: true,
                    transition: 'fade',
                    close: '',
                    scrolling: false
                            });
                $scope.$apply(); // this seems to make sure angular renders in the colorbox
                $.colorbox.resize();
            };

            $scope.yes = function () {
                $.colorbox.close();
                var newWin = window.open($scope.sourceGrantReadWizard.url);
                if (!newWin) {
                    window.location.href = $scope.sourceGrantReadWizard.url;
                }
                else {
                    newWin.focus();
                }
            };

            //init
            $scope.getSourceGrantReadWizard();
        }
    ]
);


// This is the Angular 2 part of the module
@NgModule({})
export class ClaimThanksNg2Module {}