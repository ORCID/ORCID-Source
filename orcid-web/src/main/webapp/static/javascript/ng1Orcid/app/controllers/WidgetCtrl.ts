declare var getBaseUri: any;
declare var orcidVar: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module

export const widgetCtrl = angular.module('orcidApp').controller(
    'widgetCtrl',
    [
        '$scope',
        function (
            $scope
        ){
            $scope.hash = orcidVar.orcidIdHash.substr(0, 6);
            $scope.showCode = false;

            $scope.widgetURLND = '<a href="'+ getBaseUri() + '/' + orcidVar.orcidId + '" target="orcid.widget" rel="noopener noreferrer" style="vertical-align:top;"><img src="https://orcid.org/sites/default/files/images/orcid_16x16.png" style="width:1em;margin-right:.5em;" alt="ORCID iD icon">' + orcidVar.baseDomainRmProtocall + '/' + orcidVar.orcidId + '</a>';
            
            $scope.hideWidgetCode = function(){
                $scope.showCode = false;
            };

            $scope.inputTextAreaSelectAll = function($event){
                $event.target.select();
            };
            
            $scope.toggleCopyWidget = function(){
                $scope.showCode = !$scope.showCode;
            };

        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class widgetCtrlNg2Module {}