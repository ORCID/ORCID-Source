declare var $: any; //delete
declare var orcidVar: any;

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

import { KeywordsService } 
    from '../../shared/keywords.service.ts';

import { ModalService } 
    from '../../shared/modalService.ts'; 

@Component({
    selector: 'keywords-form-ng2',
    template:  scriptTmpl("keywords-form-ng2-template")
})
export class KeywordsFormComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    bulkEditShow: any; ///
    defaultVisibility: any; ///
    form: any;
    newElementDefaultVisibility: string;
    newInput: boolean;
    orcidId: any;
 
    constructor(
        private commonSrvc: CommonService,
        private keywordsService: KeywordsService,
        private modalService: ModalService
    ) {
        this.bulkEditShow = false;
        this.defaultVisibility = null;
        this.form = {
            content: "", 
            displayIndex: "1"
        };
        this.newElementDefaultVisibility = 'PRIVATE';
        this.newInput = false;
        this.orcidId = orcidVar.orcidId; //Do not remove
  
    }

    addNew(): void {       
        var tmpObj = {
            "errors":[],
            "putCode":null,
            "content":"",
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
                
        this.form.keywords.push(tmpObj);
        this.updateDisplayIndex();
        this.newInput = true;
    };

    closeEditModal(): void{
        this.modalService.notifyOther({action:'close', moduleId: 'modalKeywordsForm'});
    };

    deleteEntry( entry ): void{
        let keywords = this.form.keywords;
        let len = keywords.length;
        while (len--) {
            if ( keywords[len] == entry ){
                keywords.splice(len,1);
            }
        }
    };

    getData(): void{
        this.keywordsService.getData()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.form = data;
   
                
                
            },
            error => {
                console.log('getKeywordsFormError', error);
            } 
        );
    };

    setBulkGroupPrivacy(priv): void{

    };

    setForm( closeAfterAction ): void {

        this.keywordsService.setData( this.form )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.form = data;
                if (this.form.errors.length == 0){
                    this.getData();
                    this.keywordsService.notifyOther();
                    if( closeAfterAction == true ) {
                        this.closeEditModal();
                    }
                }else{
                    //console.log(this.keywordsForm.errors);
                }

            },
            error => {
                //console.log('setBiographyFormError', error);
            } 
        );
        this.form.visibility = null;
    };
    
    privacyChange( obj ): any {
        this.form.visibility.visibility = obj;
        this.setForm( false );   
    };
    
    ///
    setPrivacyModal(priv, $event, country): void{        

    };
    
    swapDown(index): void{
 
    };

    swapUp(index): void{

    };

    updateDisplayIndex(): void{

    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.keywordsService.notifyObservable$.subscribe(
            (res) => {}
        );
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getData();
    };
}

/*
declare var $: any;
declare var colorbox: any;
declare var getBaseUri: any;
declare var orcidVar: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module

export const KeywordsCtrl = angular.module('orcidApp').controller(
    'KeywordsCtrl', 
    [
        '$compile', 
        '$scope', 
        '$rootScope', 
        'bioBulkSrvc', 
        'commonSrvc', 
        'emailSrvc', 
        'initialConfigService',
        'utilsService',  
        function (
            $compile, 
            $scope, 
            $rootScope, 
            bioBulkSrvc, 
            commonSrvc, 
            emailSrvc, 
            initialConfigService, 
            utilsService
        ) {
            bioBulkSrvc.initScope($scope);

            $scope.commonSrvc = commonSrvc;
            $scope.defaultVisibility = null;
            $scope.emailSrvc = emailSrvc;
            $scope.keywordsForm = null;
            $scope.modal = false;
            $scope.newElementDefaultVisibility = null;
            $scope.orcidId = orcidVar.orcidId; //Do not remove
            $scope.privacyHelp = false;
            $scope.scrollTop = 0;    
            $scope.showEdit = false;
            $scope.showElement = {};
            
            /////////////////////// Begin of verified email logic for work
            var configuration = initialConfigService.getInitialConfiguration();
            var emailVerified = false;
            var emails = {};
            var utilsService = utilsService;




            $scope.getKeywordsForm = function(){
                $.ajax({
                    url: getBaseUri() + '/my-orcid/keywordsForms.json',
                    dataType: 'json',
                    success: function(data) {
                        $scope.keywordsForm = data;
                        $scope.newElementDefaultVisibility = $scope.keywordsForm.visibility.visibility;
                        //If there is at least one element, iterate over them to see if they have the same visibility, to set the default  visibility element                
                        if($scope.keywordsForm != null 
                            && $scope.keywordsForm.keywords != null 
                            && $scope.keywordsForm.keywords.length > 0) {
                            
                            for(var i = 0; i < $scope.keywordsForm.keywords.length; i ++) {
                                var itemVisibility = null;
                                if($scope.keywordsForm.keywords[i].visibility != null && $scope.keywordsForm.keywords[i].visibility.visibility) {
                                    itemVisibility = $scope.keywordsForm.keywords[i].visibility.visibility;
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
                            $scope.defaultVisibility = $scope.keywordsForm.visibility.visibility;
                        }
                                                                        
                        $scope.$apply();
                    }
                }).fail(function(){
                    // something bad is happening!
                    console.log("error fetching keywords");
                });
            };

            $scope.setBulkGroupPrivacy = function(priv) {
                for (var idx in $scope.keywordsForm.keywords){
                    $scope.keywordsForm.keywords[idx].visibility.visibility = priv;        
                }
            };

            $scope.setKeywordsForm = function(){        
                $scope.keywordsForm.visibility = null;        
                $.ajax({
                    url: getBaseUri() + '/my-orcid/keywordsForms.json',
                    type: 'POST',
                    data:  angular.toJson($scope.keywordsForm),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        $scope.keywordsForm = data;
                        
                        if(data.errors.length == 0){
                            $scope.close();
                            $.colorbox.close();
                        }                   
                        $scope.$apply();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("KeywordsCtrl.serverValidate() error");
                });
            };

            $scope.setPrivacy = function(priv, $event) {
                $event.preventDefault();
                $scope.defaultVisibility = priv;
            };

            $scope.setPrivacyModal = function(priv, $event, keyword) {        
                var keywords = $scope.keywordsForm.keywords;        
                var len = keywords.length;
                
                $event.preventDefault();
                
                while (len--) {
                    if (keywords[len] == keyword){
                        keywords[len].visibility.visibility = priv;
                        $scope.keywordsForm.keywords = keywords;
                    }
                }
            };

            $scope.swapDown = function(index){
                var temp = null;
                var tempDisplayIndex = null;

                if (index < $scope.keywordsForm.keywords.length - 1) {
                    temp = $scope.keywordsForm.keywords[index];
                    tempDisplayIndex = $scope.keywordsForm.keywords[index]['displayIndex'];
                    temp['displayIndex'] = $scope.keywordsForm.keywords[index + 1]['displayIndex']
                    $scope.keywordsForm.keywords[index] = $scope.keywordsForm.keywords[index + 1];
                    $scope.keywordsForm.keywords[index]['displayIndex'] = tempDisplayIndex;
                    $scope.keywordsForm.keywords[index + 1] = temp;
                }
            };

            $scope.swapUp = function(index){
                var temp = null;
                var tempDisplayIndex = null;

                if (index > 0) {
                    temp = $scope.keywordsForm.keywords[index];
                    tempDisplayIndex = $scope.keywordsForm.keywords[index]['displayIndex'];
                    temp['displayIndex'] = $scope.keywordsForm.keywords[index - 1]['displayIndex']
                    $scope.keywordsForm.keywords[index] = $scope.keywordsForm.keywords[index - 1];
                    $scope.keywordsForm.keywords[index]['displayIndex'] = tempDisplayIndex;
                    $scope.keywordsForm.keywords[index - 1] = temp;
                }
            };

            $scope.updateDisplayIndex = function(){
                let idx: any;
                for (idx in $scope.keywordsForm.keywords) {
                    $scope.keywordsForm.keywords[idx]['displayIndex'] = $scope.keywordsForm.keywords.length - idx;
                }
            };
            
            $scope.getKeywordsForm();
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class KeywordsCtrlNg2Module {}
*/