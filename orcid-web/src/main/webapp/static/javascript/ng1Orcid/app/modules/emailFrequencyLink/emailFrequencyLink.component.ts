//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { EmailService } 
    from '../../shared/email.service'; 


@Component({
    selector: 'email-frecuency-link-ng2',
    template:  scriptTmpl("email-frecuency-link-ng2-template")
})
export class EmailFrecuencyLinkComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
   
    emailFrequency: any;

    constructor(
        private emailSrvc: EmailService
    ) {
        this.emailFrequency = {};
    }

    getEmailFrequencies(): void {

        this.emailSrvc.getEmailFrequencies()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(data) {
                    this.emailFrequency = data;
                }
            },
            error => {
                //console.log('getformDataError', error);
            } 
        );
 
    };

    saveEmailFrequencies(): void {
        this.emailSrvc.saveEmailFrequencies( this.emailFrequency.sendEmailFrequencyDays )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(data) {
                    this.emailFrequency = data;
                }
            },
            error => {
                //console.log('setformDataError', error);
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
        this.getEmailFrequencies();
    }; 
}

/*
//migrated

import * as angular from 'angular';
import {NgModule} from '@angular/core';

export const EmailFrequencyLinkCtrl = angular.module('orcidApp').controller(
    'EmailFrequencyLinkCtrl',
    [
        '$scope',
        '$rootScope', 
        function (
            $scope, 
            $rootScope
        ) {
            
            
            
            
            $scope.getEmailFrequencies();
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class EmailFrequencyLinkCtrlNg2Module {}
*/