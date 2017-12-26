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

import { EmailService } 
    from '../../shared/email.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

@Component({
    selector: 'emails-ng2',
    template:  scriptTmpl("emails-ng2-template")
})
export class EmailsComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    formData: any;
    emails: any;
    emailSrvc: any;

    constructor( 
        private emailService: EmailService,
        private modalService: ModalService
    ) {
        this.formData = {
            otherNames: null
        };
        this.emails = {};
    }

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
        this.emailService.getData()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.formData = data;

                if( this.formData.otherNames == null ) {
                    this.formData.otherNames = { value: null };
                }

                //console.log('this.getForm', this.formData);
            },
            error => {
                console.log('getAlsoKnownAsFormError', error);
            } 
        );
    };

    openEditModal(): void{      
        this.emailService.getEmails()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    this.modalService.notifyOther({action:'open', moduleId: 'modalAlsoKnownAsForm'});
                }else{
                    this.modalService.notifyOther({action:'open', moduleId: 'modalemailunverified'});
                }
            },
            error => {
                console.log('getEmails', error);
            } 
        );
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.emailService.notifyObservable$.subscribe(
            (res) => {
                this.getformData();
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
declare var formColorBoxResize: any;
declare var om: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const EmailsCtrl = angular.module('orcidApp').controller(
    'EmailsCtrl',
    [
        '$compile',
        '$scope', 
        'emailSrvc', 
        'prefsSrvc',
        function (
            $compile, 
            $scope, 
            emailSrvc, 
            prefsSrvc
        ){    

            $scope.emailSrvc = emailSrvc;
            $scope.showEdit = false;
            $scope.showElement = {};

            $scope.emailSrvc.getEmails();
            
            $scope.close = function(){      
                $scope.showEdit = false;
                prefsSrvc.saved = false;
                $.colorbox.close();
            }
            
            $scope.openEdit = function(){
                $scope.showEdit = true;
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
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class EmailsCtrlNg2Module {}
*/