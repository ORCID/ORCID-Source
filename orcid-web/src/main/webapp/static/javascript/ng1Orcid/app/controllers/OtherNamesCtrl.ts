declare var $: any;
declare var colorbox: any;
declare var getBaseUri: any;
declare var logAjaxError: any;
declare var orcidVar: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module

export const OtherNamesCtrl = angular.module('orcidApp').controller(
    'OtherNamesCtrl',
    [
        '$compile', 
        '$scope', 
        'bioBulkSrvc', 
        'commonSrvc', 
        'utilsService', 
        function (
            $compile,
            $scope, 
            bioBulkSrvc, 
            commonSrvc, 
            utilsService
        ) {
            var utilsService = utilsService;
 
            bioBulkSrvc.initScope($scope);  
         
            $scope.commonSrvc = commonSrvc;
            $scope.defaultVisibility = null;
            $scope.newElementDefaultVisibility = null;
            $scope.orcidId = orcidVar.orcidId; 
            $scope.otherNamesForm = null;
            $scope.privacyHelp = false;
            $scope.scrollTop = 0;
            $scope.showEdit = false;
            $scope.showElement = {};

            $scope.addNew = function() {
                $scope.otherNamesForm.otherNames.push({ url: "", urlName: "", displayIndex: "1" });
                $scope.updateDisplayIndex();            
            };

            $scope.addNewModal = function() {               
                var tmpObj = {
                    "errors":[],
                    "url":null,
                    "urlName":null,
                    "putCode":null,
                    "visibility":{
                        "errors":[],
                        "required":true,
                        "getRequiredMessage":null,
                        "visibility":$scope.newElementDefaultVisibility
                    },
                    "source":$scope.orcidId,
                    "sourceName":"", 
                    "displayIndex": 1
                };        
                $scope.otherNamesForm.otherNames.push(tmpObj);        
                $scope.updateDisplayIndex();          
                $scope.newInput = true;
            };

            $scope.close = function() {
                $scope.getOtherNamesForm();
                $scope.showEdit = false;
            };

            $scope.closeEditModal = function(){     
                $.colorbox.close();     
            };

            $scope.deleteOtherName = function(otherName){
                var otherNames = $scope.otherNamesForm.otherNames;
                var len = otherNames.length;
                while (len--) {            
                    if (otherNames[len] == otherName){                
                        otherNames.splice(len,1);
                    }
                }        
            };

            $scope.getOtherNamesForm = function(){
                $.ajax({
                    url: getBaseUri() + '/my-orcid/otherNamesForms.json',
                    dataType: 'json',
                    success: function(data) {
                        var itemVisibility;

                        $scope.otherNamesForm = data;   
                        $scope.newElementDefaultVisibility = $scope.otherNamesForm.visibility.visibility;
                        //If there is at least one element, iterate over them to see if they have the same visibility, to set the default  visibility element
                        
                        if($scope.otherNamesForm != null 
                            && $scope.otherNamesForm.otherNames != null 
                            && $scope.otherNamesForm.otherNames.length > 0) {
                            for(var i = 0; i < $scope.otherNamesForm.otherNames.length; i ++) {
                                itemVisibility = null;
                                
                                if($scope.otherNamesForm.otherNames[i].visibility != null && $scope.otherNamesForm.otherNames[i].visibility.visibility) {
                                    itemVisibility = $scope.otherNamesForm.otherNames[i].visibility.visibility;
                                }
                                /**
                                 * The default visibility should be set only when all elements have the same visibility, so, we should follow this rules: 
                                 * 
                                 * Rules: 
                                 * - If the default visibility is null:
                                 *  - If the item visibility is not null, set the default visibility to the item visibility
                                 * - If the default visibility is not null:
                                 *  - If the default visibility is not equals to the item visibility, set the default visibility to null and stop iterating 
                                 * */
                                if($scope.defaultVisibility == null) {
                                    if(itemVisibility != null) {
                                        $scope.defaultVisibility = itemVisibility;
                                    }                           
                                } else {
                                    if(itemVisibility != null) {
                                        if($scope.defaultVisibility != itemVisibility) {
                                            $scope.defaultVisibility = null;
                                            break;
                                        }
                                    } else {
                                        $scope.defaultVisibility = null;
                                        break;
                                    }
                                }                       
                            }
                        } else {
                            $scope.defaultVisibility = $scope.otherNamesForm.visibility.visibility;
                        }               
                        
                        $scope.$apply();                                
                    }
                }).fail(function(e){
                    // something bad is happening!
                    console.log("error fetching otherNames");
                    logAjaxError(e);
                });
            };
                
            $scope.openEdit = function() {
                $scope.addNew();
                $scope.showEdit = true;
            };

            $scope.openEditModal = function(){
                $scope.bulkEditShow = false;        
                $.colorbox({
                    scrolling: true,
                    html: $compile($('#edit-aka').html())($scope),
                    onLoad: function() {
                        $('#cboxClose').remove();
                        if ($scope.otherNamesForm.otherNames.length == 0){
                            $scope.addNewModal();
                            $scope.newInput = true;
                        } else {
                            $scope.updateDisplayIndex();
                        } 
                    },
                    width: utilsService.formColorBoxResize(),
                    onComplete: function() {       
                    },
                    onClosed: function() {
                        $scope.getOtherNamesForm();
                    }            
                });
                $.colorbox.resize();
            };

            $scope.setBulkGroupPrivacy = function(priv) {
                for (var idx in $scope.otherNamesForm.otherNames){
                    $scope.otherNamesForm.otherNames[idx].visibility.visibility = priv;    
                }         
            };

            $scope.setOtherNamesForm = function(){
                $scope.otherNamesForm.visibility = null;
                $.ajax({
                    url: getBaseUri() + '/my-orcid/otherNamesForms.json',
                    type: 'POST',
                    data:  angular.toJson($scope.otherNamesForm),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {                
                        $scope.otherNamesForm = data;
                        if(data.errors.length == 0){
                            $scope.close();                 
                        }
                        $.colorbox.close(); 
                        $scope.$apply();                
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("OtherNames.serverValidate() error");
                });
            };

            $scope.setPrivacy = function(priv, $event) {
                $event.preventDefault();
                $scope.defaultVisibility = priv;
            };

            $scope.setPrivacyModal = function(priv, $event, otherName) {
                var otherNames = $scope.otherNamesForm.otherNames;        
                var len = otherNames.length;

                $event.preventDefault();
                
                while (len--) {
                    if (otherNames[len] == otherName){
                        otherNames[len].visibility.visibility = priv;
                        $scope.otherNamesForm.otherNames = otherNames;
                    }
                }
            };

            $scope.swapDown = function(index){
                var temp;
                var tempDisplayIndex;
                if (index < $scope.otherNamesForm.otherNames.length - 1) {
                    temp = $scope.otherNamesForm.otherNames[index];
                    tempDisplayIndex = $scope.otherNamesForm.otherNames[index]['displayIndex'];
                    temp['displayIndex'] = $scope.otherNamesForm.otherNames[index + 1]['displayIndex']
                    $scope.otherNamesForm.otherNames[index] = $scope.otherNamesForm.otherNames[index + 1];
                    $scope.otherNamesForm.otherNames[index]['displayIndex'] = tempDisplayIndex;
                    $scope.otherNamesForm.otherNames[index + 1] = temp;
                }
            };

            $scope.swapUp = function(index){
                var temp;
                var tempDisplayIndex;
                if (index > 0) {
                    temp = $scope.otherNamesForm.otherNames[index];
                    tempDisplayIndex =$scope.otherNamesForm.otherNames[index]['displayIndex'];
                    temp['displayIndex'] = $scope.otherNamesForm.otherNames[index - 1]['displayIndex']
                    $scope.otherNamesForm.otherNames[index] = $scope.otherNamesForm.otherNames[index - 1];
                    $scope.otherNamesForm.otherNames[index]['displayIndex'] = tempDisplayIndex;
                    $scope.otherNamesForm.otherNames[index - 1] = temp;
                }
            };

            $scope.updateDisplayIndex = function(){
                let idx: any;
                for (idx in $scope.otherNamesForm.otherNames) {         
                    $scope.otherNamesForm.otherNames[idx]['displayIndex'] = $scope.otherNamesForm.otherNames.length - idx;
                }
            };
            
            $scope.getOtherNamesForm();
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class OtherNamesCtrlNg2Module {}