declare var $: any;
declare var colorbox: any;
declare var getBaseUri: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const switchUserModalCtrl = angular.module('orcidApp').controller(
    'switchUserModalCtrl',
    [
        '$compile',
        '$scope',
        function (
            $compile,
            $scope
        ){
            $scope.emails = "";
            $scope.orcidOrEmail = "";
            $scope.showSection = false;

            $scope.closeModal = function() {
                $.colorbox.close();
            };

            $scope.showSwitchErrorModal = function() {
                $.colorbox({
                    html : $compile($('#switch-error-modal').html())($scope),
                    scrolling: false,
                    onLoad: function() {
                        $('#cboxClose').remove();
                    }
                });

                setTimeout(function(){$.colorbox.resize({width:"575px"});},100);
            };

            $scope.showSwitchInvalidModal = function() {
                $.colorbox({
                    html : $compile($('#switch-imvalid-modal').html())($scope),
                    scrolling: false,
                    onLoad: function() {
                        $('#cboxClose').remove();
                    }
                });

                setTimeout(function(){$.colorbox.resize({width:"575px"});},100);
            };

            $scope.switchUserAdmin = function() {
                $.ajax({
                    url: getBaseUri()+'/admin-actions/admin-switch-user?orcidOrEmail=' + $scope.orcidOrEmail,
                    type: 'GET',
                    dataType: 'json',
                    success: function(data){
                        $scope.$apply(function(){
                            if(!$.isEmptyObject(data)) {
                                if(!$.isEmptyObject(data.errorMessg)) {
                                    $scope.orcidMap = data;
                                    $scope.showSwitchErrorModal();
                                } else {
                                    window.location.replace("./account/admin-switch-user?orcid\=" + data.orcid);
                                }
                            } else {
                                $scope.showSwitchInvalidModal();
                            }
                            $scope.orcidOrEmail='';
                        });
                    }
                }).fail(function(error) {
                    // something bad is happening!
                    console.log("Error deprecating the account");
                });
            };

            $scope.toggleSection = function(){
                $scope.showSection = !$scope.showSection;
                $('#switch_user_section').toggle();
            };
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class switchUserModalCtrlNg2Module {}