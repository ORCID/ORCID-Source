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

import { CommonService } 
    from '../../shared/commonService.ts';

import { ModalService } 
    from '../../shared/modalService.ts'; 

@Component({
    selector: 'also-known-as-form-ng2',
    template:  scriptTmpl("also-known-as-form-ng2-template")
})
export class AlsoKnownAsFormComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    defaultVisibility: any;
    emails: any;
    formData: any;
    newElementDefaultVisibility: any;
    orcidId: any;
    privacyHelp: any;
    scrollTop: any;
    showEdit: any;
    showElement: any;

    constructor( 
        private alsoKnownAsService: AlsoKnownAsService,
        private commonSrvc: CommonService,
        private modalService: ModalService
    ) {
        this.defaultVisibility = null;
        this.emails = {};
        this.formData = {
        };
        this.newElementDefaultVisibility = null;
        this.orcidId = orcidVar.orcidId; 
        this.privacyHelp = false;
        this.scrollTop = 0;
        this.showEdit = false;
        this.showElement = {};
    }

    addNew(): void {
        this.formData.otherNames.push(
            {
                displayIndex: "1", 
                url: "", 
                urlName: "", 
            }
        );
        this.updateDisplayIndex();            
    };

    closeEditModal(): void{
        this.modalService.notifyOther({action:'close', moduleId: 'modalAlsoKnownAsForm'});
    };

    deleteOtherName(otherName): void{
        let otherNames = this.formData.otherNames;
        let len = otherNames.length;
        while (len--) {            
            if (otherNames[len] == otherName){                
                otherNames.splice(len,1);
            }
        }        
    };

    getformData(): void {
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

    privacyChange( obj ): any {
        console.log('privacyChange', obj);
        this.formData.visibility.visibility = obj;
        this.setFormData( false );   
    };

    setFormData( closeAfterAction ): void {
        this.alsoKnownAsService.setData( this.formData )
        //.takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.formData = data;
                if (this.formData.errors.length == 0){
                    this.getformData();
                    this.alsoKnownAsService.notifyOther();
                    if( closeAfterAction == true ) {
                        this.closeEditModal();
                    }
                }else{
                    console.log(this.formData.errors);
                }

            },
            error => {
                console.log('setAlsoKnownAs', error);
            } 
        );
        this.formData.visibility = null;
    }

    swapDown(index): void{
        var temp;
        var tempDisplayIndex;
        if (index < this.formData.otherNames.length - 1) {
            temp = this.formData.otherNames[index];
            tempDisplayIndex = this.formData.otherNames[index]['displayIndex'];
            temp['displayIndex'] = this.formData.otherNames[index + 1]['displayIndex']
            this.formData.otherNames[index] = this.formData.otherNames[index + 1];
            this.formData.otherNames[index]['displayIndex'] = tempDisplayIndex;
            this.formData.otherNames[index + 1] = temp;
        }
    };

    swapUp(index): void{
        var temp;
        var tempDisplayIndex;
        if (index > 0) {
            temp = this.formData.otherNames[index];
            tempDisplayIndex =this.formData.otherNames[index]['displayIndex'];
            temp['displayIndex'] = this.formData.otherNames[index - 1]['displayIndex']
            this.formData.otherNames[index] = this.formData.otherNames[index - 1];
            this.formData.otherNames[index]['displayIndex'] = tempDisplayIndex;
            this.formData.otherNames[index - 1] = temp;
        }
    };

    updateDisplayIndex(): void{
        let idx: any;
        for (idx in this.formData.otherNames) {         
            this.formData.otherNames[idx]['displayIndex'] = this.formData.otherNames.length - idx;
        }
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.alsoKnownAsService.notifyObservable$.subscribe(
            (res) => {
                this.getformData();
                console.log('notified', res);
            }
        );
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getformData();
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
            $scope.formData = null;
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
                $scope.formData.otherNames.push(tmpObj);        
                $scope.updateDisplayIndex();          
                $scope.newInput = true;
            };

            

            

            $scope.getformData = function(){
                $.ajax({
                    url: getBaseUri() + '/my-orcid/formDatas.json',
                    dataType: 'json',
                    success: function(data) {
                        var itemVisibility;

                        $scope.formData = data;   
                        $scope.newElementDefaultVisibility = $scope.formData.visibility.visibility;
                        //If there is at least one element, iterate over them to see if they have the same visibility, to set the default  visibility element
                        
                        if($scope.formData != null 
                            && $scope.formData.otherNames != null 
                            && $scope.formData.otherNames.length > 0) {
                            for(var i = 0; i < $scope.formData.otherNames.length; i ++) {
                                itemVisibility = null;
                                
                                if($scope.formData.otherNames[i].visibility != null && $scope.formData.otherNames[i].visibility.visibility) {
                                    itemVisibility = $scope.formData.otherNames[i].visibility.visibility;
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
                        } else {
                            $scope.defaultVisibility = $scope.formData.visibility.visibility;
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
                for (var idx in $scope.formData.otherNames){
                    $scope.formData.otherNames[idx].visibility.visibility = priv;    
                }         
            };

            $scope.setformData = function(){
                $scope.formData.visibility = null;
                $.ajax({
                    url: getBaseUri() + '/my-orcid/formDatas.json',
                    type: 'POST',
                    data:  angular.toJson($scope.formData),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {                
                        $scope.formData = data;
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
                var otherNames = $scope.formData.otherNames;        
                var len = otherNames.length;

                $event.preventDefault();
                
                while (len--) {
                    if (otherNames[len] == otherName){
                        otherNames[len].visibility.visibility = priv;
                        $scope.formData.otherNames = otherNames;
                    }
                }
            };


           
        }
    ]
);
*/