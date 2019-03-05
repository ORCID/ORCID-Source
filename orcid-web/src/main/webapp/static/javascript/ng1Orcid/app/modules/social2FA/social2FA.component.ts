declare var $window: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit, Input } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { TwoFAStateService } 
    from '../../shared/twoFAState.service';
    
import { ShibbolethService } 
    from '../../shared/shibboleth.service'; 


@Component({
    selector: 'social2-F-A-ng2',
    template:  scriptTmpl("social2-F-A-ng2-template")
})
export class Social2FAComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    codes: any;
    verificationCode: string;
    recoveryCode: string;
    showRecoveryCodeSignIn: boolean;
    @Input() shib: boolean;

    constructor(
        private shibbolethService: ShibbolethService,
        private twoFAStateService: TwoFAStateService
    ) {
        this.codes = {};
        this.verificationCode = "";
        this.recoveryCode = "";
        this.showRecoveryCodeSignIn = false;
    }
 
    init(): void {
        if (this.shib) {
            this.initShib2FA();
        } else {
            this.initSocial2FA();
        }
    };
    
    toggleRecoveryCodeSignIn(): void {
        this.showRecoveryCodeSignIn = true;
    };
    
    initSocial2FA(): void {
        this.twoFAStateService.init()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.codes = data;
            },
            error => {
                //console.log('getWebsitesFormError', error);
            } 
        );
    };
    
    initShib2FA(): void {
        this.shibbolethService.init()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
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
        if (this.shib) {
            this.submitShib2FA();
        } else {
            this.submitSocial2FA();
        }
    };
    
    submitSocial2FA(): void {    
        this.twoFAStateService.submitCode( this.codes )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
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
                //console.log('getWebsitesFormError', error);
            } 
        );
    };
    
    submitShib2FA(): void {
        this.shibbolethService.submitCode( this.codes )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
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
