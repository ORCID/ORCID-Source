declare var colorbox: any;
declare var formColorBoxResize: any;
declare var om: any;
declare var $: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const EmailsCtrl = angular.module('orcidApp').controller('EmailsCtrl',['$scope', 'emailSrvc', '$compile','prefsSrvc' ,function ($scope, emailSrvc, $compile, prefsSrvc){    
    $scope.emailSrvc = emailSrvc;
    $scope.showEdit = false;
    $scope.showElement = {};

    emailSrvc.getEmails();
    
    $scope.openEdit = function(){
        $scope.showEdit = true;
    }
    
    $scope.close = function(){      
        $scope.showEdit = false;
        prefsSrvc.saved = false;
        $.colorbox.close();
    }
    
    $scope.openEditModal = function(){
        
        var HTML = '<div class="lightbox-container" style="position: static">\
                        <div class="edit-record edit-record-emails" style="position: static">\
                            <div class="row">\
                                <div class="col-md-12 col-sm-12 col-xs-12">\
                                        <h1 class="lightbox-title pull-left">'+ om.get("manage.edit.emails") +'</h1>\
                                </div>\
                            </div>\
                            <div class="row">\
                                <div class="col-md-12 col-xs-12 col-sm-12" style="position: static">\
                                    <table class="settings-table" style="position: static">\
                                        <tr>' +
                                            $('#edit-emails').html()
                                      +'</tr>\
                                    </table>\
                                </div>\
                            </div>\
                            <div class="row">\
                                <div class="col-md-12 col-sm-12 col-xs-12">\
                                    <a ng-click="close()" class="cancel-option pull-right">'+om.get("manage.email.close")+'</a>\
                                </div>\
                            </div>\
                        </div>\
                    </div>';  
        
        $scope.emailSrvc.popUp = true;
        
        $.colorbox({
            scrolling: true,
            html: $compile(HTML)($scope),
            onLoad: function() {                
                $('#cboxClose').remove();
            },
            width: formColorBoxResize(),
            onComplete: function() {
                $.colorbox.resize();
            },
            onClosed: function() {
                $scope.emailSrvc.popUp = false;        
            }            
        });
    }
    
}]);

// This is the Angular 2 part of the module
@NgModule({})
export class EmailsCtrlNg2Module {}