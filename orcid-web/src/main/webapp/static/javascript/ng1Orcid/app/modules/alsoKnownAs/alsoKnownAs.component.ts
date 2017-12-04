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

import { AlsoKnownAsService } 
    from '../../shared/alsoKnownAs.service.ts';

import { EmailService } 
    from '../../shared/emailService.ts';

import { ModalService } 
    from '../../shared/modalService.ts'; 

@Component({
    selector: 'also-known-as-ng2',
    template:  scriptTmpl("also-known-as-ng2-template")
})
export class AlsoKnownAsComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    formData: any;
    emails: any;
    emailSrvc: any;

    constructor( 
        private alsoKnownAsService: AlsoKnownAsService,
        private emailService: EmailService,
        private modalService: ModalService
    ) {

        this.formData = {
        };
        this.emails = {};
    }

    addNew(): void {
        this.otherNamesForm.otherNames.push({ url: "", urlName: "", displayIndex: "1" });
        this.updateDisplayIndex();            
    };

    close(): void {
        this.getOtherNamesForm();
        this.showEdit = false;
    };

    closeEditModal(): void{     
        $.colorbox.close();     
    };

    $scope.deleteOtherName = function(otherName){
        var otherNames = this.otherNamesForm.otherNames;
        var len = otherNames.length;
        while (len--) {            
            if (otherNames[len] == otherName){                
                otherNames.splice(len,1);
            }
        }        
    };

    getForm(): void {
        this.alsoKnownAsService.getData()
        //.takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.formData = data;
                console.log('this.getForm', this.formData);
            },
            error => {
                console.log('getAlsoKnownAsFormError', error);
            } 
        );
    };

    openEditModal(): void{      
        this.emailService.getEmails()
        //.takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    this.modalService.notifyOther({action:'open', moduleId: 'modalalsoKnownAsForm'});
                }else{
                    this.modalService.notifyOther({action:'open', moduleId: 'modalemailunverified'});
                }
            },
            error => {
                console.log('getEmails', error);
            } 
        );
        /*
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
	    */
    };

    swapDown(index): void{
        var temp;
        var tempDisplayIndex;
        if (index < this.otherNamesForm.otherNames.length - 1) {
            temp = this.otherNamesForm.otherNames[index];
            tempDisplayIndex = this.otherNamesForm.otherNames[index]['displayIndex'];
            temp['displayIndex'] = this.otherNamesForm.otherNames[index + 1]['displayIndex']
            this.otherNamesForm.otherNames[index] = this.otherNamesForm.otherNames[index + 1];
            this.otherNamesForm.otherNames[index]['displayIndex'] = tempDisplayIndex;
            this.otherNamesForm.otherNames[index + 1] = temp;
        }
    };

    swapUp(index): void{
        var temp;
        var tempDisplayIndex;
        if (index > 0) {
            temp = this.otherNamesForm.otherNames[index];
            tempDisplayIndex =this.otherNamesForm.otherNames[index]['displayIndex'];
            temp['displayIndex'] = this.otherNamesForm.otherNames[index - 1]['displayIndex']
            this.otherNamesForm.otherNames[index] = this.otherNamesForm.otherNames[index - 1];
            this.otherNamesForm.otherNames[index]['displayIndex'] = tempDisplayIndex;
            this.otherNamesForm.otherNames[index - 1] = temp;
        }
    };

    updateDisplayIndex(): void{
        let idx: any;
        for (idx in this.otherNamesForm.otherNames) {         
            this.otherNamesForm.otherNames[idx]['displayIndex'] = this.otherNamesForm.otherNames.length - idx;
        }
    };




    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.alsoKnownAsService.notifyObservable$.subscribe(
            (res) => {
                this.getForm();
                console.log('notified', res);
            }
        );
    };

    ngOnDestroy() {
        //this.ngUnsubscribe.next();
        //this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getForm();
    };

}


/*
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


           
        }
    ]
);