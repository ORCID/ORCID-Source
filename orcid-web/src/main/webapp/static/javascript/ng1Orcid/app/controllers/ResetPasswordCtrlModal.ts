declare var $: any;
declare var colorbox: any;
declare var getBaseUri: any;
declare var logAjaxError: any;
declare var om: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module

export const resetPasswordCtrlModal = angular.module('orcidApp').controller(
    'resetPasswordCtrlModal',
    [
        '$compile', 
        '$scope', 
        function (
            $compile,
            $scope
        ) {
            $scope.params = {orcidOrEmail:'',password:''};
            $scope.result = '';
            $scope.showSection = false;

            $scope.closeModal = function() {
                $scope.params.orcidOrEmail='';
                $scope.params.password='';
                $scope.result= '';
                $.colorbox.close();
            };

            $scope.confirmResetPassword = function(){
                if($scope.params.orcidOrEmail != '' 
                    && $scope.params.password != ''
                ) {
                    $.colorbox(
                        {
                            html : $compile($('#confirm-reset-password').html())($scope),
                            scrolling: true,
                            onLoad: function() {
                                $('#cboxClose').remove();
                            }
                        }
                    );

                    $.colorbox.resize({width:"450px" , height:"150px"});
                }
            };

            $scope.randomString = function() {
                $scope.result = '';
                $.ajax({
                    url: getBaseUri()+'/admin-actions/generate-random-string.json',
                    type: 'GET',
                    dataType: 'text',
                    success: function(data){
                        $scope.$apply(function(){
                            $scope.params.password=data;
                        });
                    }
                }).fail(function(e) {
                    // something bad is happening!
                    console.log("Error generating random string");
                    logAjaxError(e);
                });
            };

            $scope.resetPassword = function(){
                $scope.result = '';
                $.ajax({
                    url: getBaseUri()+'/admin-actions/reset-password.json',
                    type: 'POST',
                    data: angular.toJson($scope.params),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'text',
                    success: function(data){
                        $scope.$apply(function(){
                            $scope.result=data;
                            $scope.params.orcidOrEmail='';
                            $scope.params.password='';
                        });
                        $scope.closeModal();
                    }
                }).fail(function(error) {
                    // something bad is happening!
                    console.log("Error generating random string");
                });
            };

            $scope.toggleSection = function(){
                $scope.showSection = !$scope.showSection;
                $('#reset_password_section').toggle();
            };
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class resetPasswordCtrlModalNg2Module {}