//Migrated

declare var orcidGA: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const LinkAccountController = angular.module('orcidApp').controller(
    'LinkAccountController',
    [
        '$scope',
        '$timeout', 
        'discoSrvc', 
        function (
            $scope,
            $timeout, 
            discoSrvc
        ){
            
            $scope.gaString = null; 
            $scope.loadedFeed = false;
            $scope.requestInfoForm = null; 
            
            $scope.$watch(function() { return discoSrvc.feed; }, function(){
                $scope.idpName = discoSrvc.getIdPName($scope.entityId);
                if(discoSrvc.feed != null) {
                    $scope.loadedFeed = true;
                }
            });

            $scope.loadRequestInfoForm = function() {
                $.ajax({
                    url: getBaseUri() + '/oauth/custom/authorize/get_request_info_form.json',
                    type: 'GET',
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        $timeout(function() {
                            if(data){                                                                                        
                                $scope.requestInfoForm = data;              
                                $scope.gaString = orcidGA.buildClientString($scope.requestInfoForm.memberName, $scope.requestInfoForm.clientName);
                            }
                        });
                    }
                }).fail(function() {
                    console.log("An error occured initializing the form.");
                });
            };
            
            $scope.setEntityId = function(entityId) {
                $scope.entityId = entityId;
            };

            // Init
            $scope.loadRequestInfoForm();
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class LinkAccountControllerNg2Module {}