declare var orcidGA: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const LinkAccountController = angular.module('orcidApp').controller(
    'LinkAccountController',
    [
        '$scope', 
        'discoSrvc', 
        function (
            $scope, 
            discoSrvc
        ){
    
            $scope.loadedFeed = false;
            
            $scope.$watch(function() { return discoSrvc.feed; }, function(){
                $scope.idpName = discoSrvc.getIdPName($scope.entityId);
                if(discoSrvc.feed != null) {
                    $scope.loadedFeed = true;
                }
            });

            $scope.linkAccount = function(idp, linkType) {
                var eventAction = linkType === 'shibboleth' ? 'Sign-In-Link-Federated' : 'Sign-In-Link-Social';
                orcidGA.gaPush(['send', 'event', 'Sign-In-Link', eventAction, idp]);
                return false;
            };
            
            $scope.setEntityId = function(entityId) {
                $scope.entityId = entityId;
            };
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class LinkAccountControllerNg2Module {}