declare var $: any; //delete

import { NgFor, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

import { CommonService } 
    from '../../shared/commonService.ts'; 

import { ConfigurationService } 
    from '../../shared/configurationService.ts';

import { CountryService } 
    from '../../shared/countryService.ts'; 

import { ModalService } 
    from '../../shared/modalService.ts'; 

/*
import * as angular from 'angular';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common'; 
import { Component, Inject, Injector, Input, ViewChild, Directive, ElementRef } from '@angular/core';
import { NgModule } from '@angular/core';


*/



@Component({
    selector: 'country-ng2',
    template:  scriptTmpl("country-ng2-template")
})
export class CountryComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

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
        private commonService: CommonService,
        private countryService: CountryService,
        private modalService: ModalService
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

    getCountryForm(): void {
        /*
        this.countryService.getCountryData()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.countryForm = data;
                console.log('this.countryForm', this.countryForm);

            },
            error => {
                console.log('getBiographyFormError', error);
            } 
        );
        */
        /*
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
        */
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
            //data:  angular.toJson(this.countryForm),
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

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        console.log('country init');
        //this.getCountryForm();
    };

}
