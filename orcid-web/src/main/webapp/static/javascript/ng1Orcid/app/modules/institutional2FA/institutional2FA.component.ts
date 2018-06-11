//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { ShibbolethService } 
    from '../../shared/shibboleth.service.ts'; 

import { PreferencesService } 
    from '../../shared/preferences.service.ts'; 


@Component({
    selector: 'institutional2-f-a-ng2',
    template:  scriptTmpl("institutional2-f-a-ng2-template")
})
export class Institutional2FAComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    codes: any;
    verificationCode: any;
    recoveryCode: any;

    constructor(
        private shibbolethService: ShibbolethService,
        private prefsSrvc: PreferencesService
    ) {
        this.codes = {};
        this.verificationCode = "";
        this.recoveryCode = "";
    }

    init(): void {
        $('#enterRecoveryCode').click(function() {
            $('#recoveryCodeSignin').show();
        });

        this.shibbolethService.init()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.codes = data;
            },
            error => {
                //console.log('getAlsoKnownAsFormError', error);
            } 
        );
    };

    submitCode(): void {

        this.shibbolethService.submitCode( this.codes )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.codes = data;
                if (data.errors.length == 0) {
                    window.location.href = data.redirectUrl;

                } else {
                    this.verificationCode = "";
                    this.recoveryCode = "";
                }
            },
            error => {
                //console.log('getAlsoKnownAsFormError', error);
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
        this.init();
    }; 
}
