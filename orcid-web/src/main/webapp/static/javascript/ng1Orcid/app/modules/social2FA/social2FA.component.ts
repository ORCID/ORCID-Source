declare var $window: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

import { TwoFAStateService } 
    from '../../shared/twoFAState.service.ts';


@Component({
    selector: 'social2-F-A-ng2',
    template:  scriptTmpl("social2-F-A-ng2-template")
})
export class Social2FAComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    codes: any;
    verificationCode: string;
    recoveryCode: string;

    constructor(
        private twoFAStateService: TwoFAStateService
    ) {
        this.codes = {};
        this.verificationCode = "";
        this.recoveryCode = "";
    }

    init(): void {
        $('#enterRecoveryCode').click(function() {
            $('#recoveryCodeSignin').show();
        });

        this.twoFAStateService.init()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.codes = data;
            },
            error => {
                //console.log('getWebsitesFormError', error);
            } 
        );
    };

    submitCode(): void {
        this.twoFAStateService.submitCode( this.codes )
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
                //console.log('getWebsitesFormError', error);
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
