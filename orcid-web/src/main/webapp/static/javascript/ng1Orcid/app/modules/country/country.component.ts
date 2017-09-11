declare var $: any;

import * as angular from 'angular';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common'; 
import { Component, Inject, Injector, Input, ViewChild, Directive, ElementRef } from '@angular/core';
import { NgModule } from '@angular/core';

import { CommonService } from '../../shared/commonService.ts'; 

// This is the Angular 1 part of the module
/*
export const CountryCtrl = angular.module('orcidApp').controller(
    'CountryCtrl', 
    [
        '$scope', 
        '$rootScope', 
        '$compile', 
        'bioBulkSrvc', 
        'commonSrvc', 
        'emailSrvc', 
        'initialConfigService', 
        'utilsService', 
        function (
            $scope, 
            $rootScope, 
            $compile, 
            bioBulkSrvc, 
            commonSrvc, 
            emailSrvc, 
            initialConfigService, 
            utilsService
        ) {
            bioBulkSrvc.initScope($scope);
            $scope.commonSrvc = commonSrvc;
            $scope.countryForm = null;
            $scope.defaultVisibility = null;
            $scope.emailSrvc = emailSrvc;
            $scope.newElementDefaultVisibility = null;
            $scope.newInput = false;    
            $scope.orcidId = orcidVar.orcidId;
            $scope.primaryElementIndex = null;
            $scope.privacyHelp = false;
            $scope.scrollTop = 0;   
            $scope.showEdit = false;
            $scope.showElement = {};

            /////////////////////// Begin of verified email logic for work
            var configuration = initialConfigService.getInitialConfiguration();
            var emailVerified = false;
            var emails = {};
            var utilsService = utilsService;

            var showEmailVerificationModal = function(){
                $rootScope.$broadcast('emailVerifiedObj', {flag: emailVerified, emails: emails});
            };

            $scope.emailSrvc.getEmails(
                function(data) {
                    emails = data.emails;
                    if( $scope.emailSrvc.getEmailPrimary().verified == true ) {
                        emailVerified = true;
                    }
                }
            );
            /////////////////////// End of verified email logic for work

            $scope.addNewModal = function() {       
                var tmpObj = {
                    "errors":[],
                    "iso2Country": null,
                    "countryName":null,
                    "putCode":null,
                    "visibility":{
                        "errors":[],
                        "required":true,
                        "getRequiredMessage":null,
                        "visibility":$scope.newElementDefaultVisibility
                    },
                    "displayIndex":1,
                    "source":$scope.orcidId,
                    "sourceName":""
                };
                $scope.countryForm.addresses.push(tmpObj);
                $scope.updateDisplayIndex();
                $scope.newInput = true; 
            };

            $scope.closeEditModal = function(){
                $.colorbox.close();
            };

            $scope.closeModal = function(){     
                $.colorbox.close();
            };

            $scope.deleteCountry = function(country){
                var countries = $scope.countryForm.addresses;
                var len = countries.length;
                while (len--) {
                    if (countries[len] == country){
                        countries.splice(len,1);
                        $scope.countryForm.addresses = countries;
                    }       
                }
            };

            $scope.getCountryForm = function(){
                $.ajax({
                    url: getBaseUri() + '/account/countryForm.json',
                    dataType: 'json',
                    success: function(data) {
                        $scope.countryForm = data;                
                        $scope.newElementDefaultVisibility = $scope.countryForm.visibility.visibility;
                        //If there is at least one element, iterate over them to see if they have the same visibility, to set the default  visibility element
                        if($scope.countryForm != null && $scope.countryForm.addresses != null && $scope.countryForm.addresses.length > 0) {
                            var highestDisplayIndex = null;
                            var itemVisibility = null;
                            
                            for(var i = 0; i < $scope.countryForm.addresses.length; i ++) {
                                if($scope.countryForm.addresses[i].visibility != null && $scope.countryForm.addresses[i].visibility.visibility) {
                                    itemVisibility = $scope.countryForm.addresses[i].visibility.visibility;
                                }
                                /**
                                 * The default visibility should be set only when all elements have the same visibility, so, we should follow this rules: 
                                 * 
                                 * Rules: 
                                 * - If the default visibility is null:
                                 *  - If the item visibility is not null, set the default visibility to the item visibility
                                 * - If the default visibility is not null:
                                 *  - If the default visibility is not equals to the item visibility, set the default visibility to null and stop iterating 
                                 * * /
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
                            //We have to iterate on them again to select the primary address
                            for(var i = 0; i < $scope.countryForm.addresses.length; i ++) {
                                //Set the primary element based on the display index
                                if($scope.primaryElementIndex == null || highestDisplayIndex < $scope.countryForm.addresses[i].displayIndex) {
                                    $scope.primaryElementIndex = i;
                                    highestDisplayIndex = $scope.countryForm.addresses[i].displayIndex;
                                }
                            }
                        } else {
                            $scope.defaultVisibility = $scope.countryForm.visibility.visibility;                    
                        }     
                        $scope.$apply();                
                    }
                }).fail(function(e){
                    // something bad is happening!
                    console.log("error fetching external identifiers");
                    logAjaxError(e);
                });
            };

            $scope.openEditModal = function() {
                
                if(emailVerified === true || configuration.showModalManualEditVerificationEnabled == false){
                    $scope.bulkEditShow = false;
                    
                    $.colorbox({
                        html: $compile($('#edit-country').html())($scope),
                        scrolling: true,
                        onLoad: function() {
                            $('#cboxClose').remove();
                            if ($scope.countryForm.addresses.length == 0){                  
                                $scope.addNewModal();
                            } else {
                                if ($scope.countryForm.addresses.length == 1){
                                    if($scope.countryForm.addresses[0].source == null){
                                        $scope.countryForm.addresses[0].source = $scope.orcidId;
                                        $scope.countryForm.addresses[0].sourceName = "";
                                    }
                                }
                                $scope.updateDisplayIndex();
                            }                
                        },
             
                        width: utilsService.formColorBoxResize(),
                        onComplete: function() {      
                        },
                        onClosed: function() {
                            $scope.getCountryForm();
                        }            
                    });
                    $.colorbox.resize();
                }else{
                    showEmailVerificationModal();
                }
            };

            $scope.setBulkGroupPrivacy = function(priv) {
                for (var idx in $scope.countryForm.addresses){
                    $scope.countryForm.addresses[idx].visibility.visibility = priv;        
                }
            };

            $scope.setCountryForm = function(){
                $scope.countryForm.visibility = null;
                $.ajax({
                    contentType: 'application/json;charset=UTF-8',
                    data:  angular.toJson($scope.countryForm),
                    dataType: 'json',
                    type: 'POST',
                    url: getBaseUri() + '/account/countryForm.json',
                    success: function(data) {
                        $scope.countryForm = data;
                        if ($scope.countryForm.errors.length == 0){
                            $.colorbox.close();
                            $scope.getCountryForm();
                        }else{
                            console.log($scope.countryForm.errors);
                        }
                        
                        $scope.$apply();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("CountryCtrl.serverValidate() error");
                });
            };
            
            $scope.setPrivacy = function(priv, $event) {
                $event.preventDefault();
                $scope.defaultVisibility = priv;
            };
            
            $scope.setPrivacyModal = function(priv, $event, country) {        
                var countries = $scope.countryForm.addresses;        
                var len = countries.length;   

                $event.preventDefault();

                while (len--) {
                    if (countries[len] == country){            
                        countries[len].visibility.visibility = priv;
                        $scope.countryForm.addresses = countries;
                    }
                }
            };
            
            $scope.swapDown = function(index){
                var temp = null;
                var tempDisplayIndex = null;
                if (index < $scope.countryForm.addresses.length - 1) {
                    temp = $scope.countryForm.addresses[index];
                    tempDisplayIndex = $scope.countryForm.addresses[index]['displayIndex'];
                    temp['displayIndex'] = $scope.countryForm.addresses[index + 1]['displayIndex']
                    
                    $scope.countryForm.addresses[index] = $scope.countryForm.addresses[index + 1];
                    $scope.countryForm.addresses[index]['displayIndex'] = tempDisplayIndex;
                    $scope.countryForm.addresses[index + 1] = temp;
                }
            };

            $scope.swapUp = function(index){
                var temp = null;
                var tempDisplayIndex = null;
                if (index > 0) {
                    temp = $scope.countryForm.addresses[index];
                    tempDisplayIndex = $scope.countryForm.addresses[index]['displayIndex'];
                    temp['displayIndex'] = $scope.countryForm.addresses[index - 1]['displayIndex']
                    
                    $scope.countryForm.addresses[index] = $scope.countryForm.addresses[index - 1];
                    $scope.countryForm.addresses[index]['displayIndex'] = tempDisplayIndex;
                    $scope.countryForm.addresses[index - 1] = temp;
                }
            };

            $scope.updateDisplayIndex = function():any {
                let displayIndex: any;
                let countryFormAddressesLength: any;
                let idx: any;
                
                for ( idx in $scope.countryForm.addresses ){
                    countryFormAddressesLength = $scope.countryForm.addresses.length;
                    displayIndex = countryFormAddressesLength - idx;
                    $scope.countryForm.addresses[idx]['displayIndex'] = displayIndex;
                }
            };

            $scope.getCountryForm();

        }
    ]
);
*/


//var SessionService = injector.get(SessionService);

@Component({
    selector: 'country-ng2',
    template:  scriptTmpl("country-ng2-template")
})
export class CountryComponent {
    @ViewChild('modalng2') modalngcomponent;

    bulkEditShow: any;
    countryForm: any;
    defaultVisibility: any;
    emailSrvc: any;
    newElementDefaultVisibility: any;
    newInput: any;
    orcidId: any;
    primaryElementIndex: any;  
    showEdit: any;
    showElement: any;

    constructor( 
        private commonService: CommonService
    ) {
        //console.log('this.commonSrvc', this.commonSrvc2);
        //console.log('test service ', commonService.getHeroes()); 


        this.bulkEditShow = false;
        this.countryForm = null;
        this.defaultVisibility = null;
        //this.emailSrvc = emailSrvc;
        this.newElementDefaultVisibility = null;
        this.newInput = false;    
        this.orcidId = orcidVar.orcidId;
        this.primaryElementIndex = null;   
        this.showEdit = false;
        this.showElement = {};
    }

    /*
    showEmailVerificationModal(): void{
        $rootScope.$broadcast('emailVerifiedObj', {flag: emailVerified, emails: emails});
    };

    $scope.emailSrvc.getEmails(
        function(data) {
            emails = data.emails;
            if( $scope.emailSrvc.getEmailPrimary().verified == true ) {
                emailVerified = true;
            }
        }
    );
    */

    addNewModal(): void {       
        var tmpObj = {
            "errors":[],
            "iso2Country": null,
            "countryName":null,
            "putCode":null,
            "visibility":{
                "errors":[],
                "required":true,
                "getRequiredMessage":null,
                "visibility":this.newElementDefaultVisibility
            },
            "displayIndex":1,
            "source":this.orcidId,
            "sourceName":""
        };
        this.countryForm.addresses.push(tmpObj);
        this.updateDisplayIndex();
        this.newInput = true; 
    };

    closeEditModal(): void{
        $.colorbox.close();
    };

    closeModal(): void{     
        $.colorbox.close();
    };

    deleteCountry(country): void{
        var countries = this.countryForm.addresses;
        var len = countries.length;
        while (len--) {
            if (countries[len] == country){
                countries.splice(len,1);
                this.countryForm.addresses = countries;
            }       
        }
    };

    getCountryForm(): void{
        $.ajax({
            url: getBaseUri() + '/account/countryForm.json',
            dataType: 'json',
            success: function(data) {
                this.countryForm = data;                
                this.newElementDefaultVisibility = this.countryForm.visibility.visibility;
                //If there is at least one element, iterate over them to see if they have the same visibility, to set the default  visibility element
                if( this.countryForm != null 
                    && this.countryForm.addresses != null 
                    && this.countryForm.addresses.length > 0) {
                    var highestDisplayIndex = null;
                    var itemVisibility = null;
                    
                    for(var i = 0; i < this.countryForm.addresses.length; i ++) {
                        if( this.countryForm.addresses[i].visibility != null 
                            && this.countryForm.addresses[i].visibility.visibility ) {
                            itemVisibility = this.countryForm.addresses[i].visibility.visibility;
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
                        if(this.defaultVisibility == null) {
                            if(itemVisibility != null) {
                                this.defaultVisibility = itemVisibility;
                            }                           
                        } else {
                            if(itemVisibility != null) {
                                if(this.defaultVisibility != itemVisibility) {
                                    this.defaultVisibility = null;
                                    break;
                                }
                            } else {
                                this.defaultVisibility = null;
                                break;
                            }
                        }                                                                   
                    }
                    //We have to iterate on them again to select the primary address
                    for(var i = 0; i < this.countryForm.addresses.length; i ++) {
                        //Set the primary element based on the display index
                        if(this.primaryElementIndex == null 
                            || highestDisplayIndex < this.countryForm.addresses[i].displayIndex) {
                            this.primaryElementIndex = i;
                            highestDisplayIndex = this.countryForm.addresses[i].displayIndex;
                        }
                    }
                } else {
                    this.defaultVisibility = this.countryForm.visibility.visibility;                    
                }     
                //$scope.$apply();                
            }
        }).fail(function(e){
            // something bad is happening!
            console.log("error fetching external identifiers");
            logAjaxError(e);
        });
    };

    /*
    openEditModal(): void{
        console.log('open edit modal 2');
        this.showEdit = true;
        
        //if(emailVerified === true || configuration.showModalManualEditVerificationEnabled == false){
            
            this.bulkEditShow = false;
            $.colorbox({
                //html: $compile($('#edit-country').html())(this),
                html: scriptTmpl("edit-country"),
                //html: 'testcolorbox',
                scrolling: true,
                onLoad: function() {
                    $('#cboxClose').remove();
                    /*
                    if ($scope.countryForm.addresses.length == 0){                  
                        $scope.addNewModal();
                    } else {
                        if ($scope.countryForm.addresses.length == 1){
                            if($scope.countryForm.addresses[0].source == null){
                                $scope.countryForm.addresses[0].source = $scope.orcidId;
                                $scope.countryForm.addresses[0].sourceName = "";
                            }
                        }
                        $scope.updateDisplayIndex();
                    } 
                    * /               
                },
     
                //width: utilsService.formColorBoxResize(),
                onComplete: function() {      
                },
                onClosed: function() {
                    //this.getCountryForm();
                }            
            });
            $.colorbox.resize();
            
        /*}else{
            showEmailVerificationModal();
        }* /
    };
    */

    setBulkGroupPrivacy(priv): void{
        for (var idx in this.countryForm.addresses){
            this.countryForm.addresses[idx].visibility.visibility = priv;        
        }
    };

    setCountryForm(): void{
        this.countryForm.visibility = null;
        $.ajax({
            contentType: 'application/json;charset=UTF-8',
            data:  angular.toJson(this.countryForm),
            dataType: 'json',
            type: 'POST',
            url: getBaseUri() + '/account/countryForm.json',
            success: function(data) {
                this.countryForm = data;
                if (this.countryForm.errors.length == 0){
                    $.colorbox.close();
                    this.getCountryForm();
                }else{
                    console.log(this.countryForm.errors);
                }
                
                //$scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("CountryCtrl.serverValidate() error");
        });
    };
    
    setPrivacy(priv, $event): void{
        $event.preventDefault();
        this.defaultVisibility = priv;
    };
    
    setPrivacyModal(priv, $event, country): void{        
        var countries = this.countryForm.addresses;        
        var len = countries.length;   

        $event.preventDefault();

        while (len--) {
            if (countries[len] == country){            
                countries[len].visibility.visibility = priv;
                this.countryForm.addresses = countries;
            }
        }
    };
    
    swapDown(index): void{
        var temp = null;
        var tempDisplayIndex = null;
        if (index < this.countryForm.addresses.length - 1) {
            temp = this.countryForm.addresses[index];
            tempDisplayIndex = this.countryForm.addresses[index]['displayIndex'];
            temp['displayIndex'] = this.countryForm.addresses[index + 1]['displayIndex']
            
            this.countryForm.addresses[index] = this.countryForm.addresses[index + 1];
            this.countryForm.addresses[index]['displayIndex'] = tempDisplayIndex;
            this.countryForm.addresses[index + 1] = temp;
        }
    };

    swapUp(index): void{
        var temp = null;
        var tempDisplayIndex = null;
        if (index > 0) {
            temp = this.countryForm.addresses[index];
            tempDisplayIndex = this.countryForm.addresses[index]['displayIndex'];
            temp['displayIndex'] = this.countryForm.addresses[index - 1]['displayIndex']
            
            this.countryForm.addresses[index] = this.countryForm.addresses[index - 1];
            this.countryForm.addresses[index]['displayIndex'] = tempDisplayIndex;
            this.countryForm.addresses[index - 1] = temp;
        }
    };

    updateDisplayIndex(): void{
        let displayIndex: any;
        let countryFormAddressesLength: any;
        let idx: any;
        
        for ( idx in this.countryForm.addresses ){
            countryFormAddressesLength = this.countryForm.addresses.length;
            displayIndex = countryFormAddressesLength - idx;
            this.countryForm.addresses[idx]['displayIndex'] = displayIndex;
        }
    };

    //$scope.getCountryForm();

}