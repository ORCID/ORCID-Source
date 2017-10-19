//Import all the angular components

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

import { NameService } 
    from '../../shared/nameService.ts'; 

import { ConfigurationService } 
    from '../../shared/configurationService.ts';

import { EmailService } 
    from '../../shared/emailService.ts';

import { ModalService } 
    from '../../shared/modalService.ts'; 

@Component({
    selector: 'name-ng2',
    template:  scriptTmpl("name-ng2-template")
})
export class NameComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    nameForm: any;
    configuration: any;
    emails: any;
    emailSrvc: any;
    emailVerified: any;
    lengthError: any;
    privacyHelp: boolean;
    showEdit: any;

    constructor(
        private nameService: NameService,
        private configurationService: ConfigurationService,
        private emailService: EmailService,
        private modalService: ModalService
    ) {
        this.nameForm = {

        };
        
        this.emails = {};
        this.emailVerified = false; //change to false once service is ready
        this.lengthError = false;
        this.privacyHelp = false;
        this.showEdit = false;
    }

    cancel(): void {
        this.getNameForm();
        this.showEdit = false;
    };

    close(): void {
        this.showEdit = false;
    };

    getNameForm(): void {
        this.nameService.getData()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.nameForm = data;
                console.log('this.nameForm', this.nameForm);
            },
            error => {
                console.log('getNameForm Error', error);
            } 
        );
    };

    privacyChange( obj ): any {
        this.nameForm.visiblity.visibility = obj;
        this.setNameForm();   
    };

    setNameForm(): any{
        this.nameService.setData( this.nameForm )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.nameForm = data;
                //console.log('this.nameForm response', this.nameForm);
                this.close();
            },
            error => {
                console.log('setNameForm Error', error);
            } 
        );
    };

    ///
    setNamesVisibility(priv, $event): void {
        $event.preventDefault();
        this.nameForm.namesVisibility.visibility = priv;
    };
    
    toggleEdit(): void {

        this.emailService.getEmails()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    this.showEdit = !this.showEdit;
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
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getNameForm();
        this.configuration = this.configurationService.getInitialConfiguration();
    }; 
}

/*
declare var getBaseUri: any;
declare var orcidVar: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module

export const NameCtrl = angular.module('orcidApp').controller(
    'NameCtrl', 
    [
        '$compile',
        '$scope', 
        function NameCtrl(
            $compile,
            $scope
        ) {


           

            $scope.setNamesVisibility = function(priv, $event) {
                $event.preventDefault();
                $scope.nameForm.namesVisibility.visibility = priv;
            };

            $scope.getNameForm();
        }]
);

// This is the Angular 2 part of the module
@NgModule({})
export class NameCtrlNg2Module {}
*/