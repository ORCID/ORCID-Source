//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { CommonService }
    from '../../shared/common.service.ts';

import { PasswordService } 
    from '../../shared/password.service.ts'; 


@Component({
    selector: 'reset-password-ng2',
    template:  scriptTmpl("reset-password-ng2-template")
})
export class ResetPasswordComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    displayError: boolean;
    resetPasswordForm: any;

    constructor(
        private commonSrvc: CommonService,
        private passwordService: PasswordService
    ) {
        this.displayError = false;
        this.resetPasswordForm = {};
    }

    getResetPasswordForm(): void {
        this.passwordService.getResetPasswordForm()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.resetPasswordForm = data;
            },
            error => {
                console.log('error fetching password-reset.json');
            } 
        );
 
    };

    postPasswordReset(): void {
        var urlParts = window.location.href.split('/');
        var encryptedEmail = urlParts[urlParts.length -1];
        this.resetPasswordForm.encryptedEmail = encryptedEmail;
        this.passwordService.postPasswordReset( this.resetPasswordForm )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                if (data.successRedirectLocation != null) {
                    window.location.href = data.successRedirectLocation;
                } else {
                    this.commonSrvc.copyErrorsLeft(this.resetPasswordForm, data);
                }

            },
            error => {
                console.log('error posting to reset-password-email.json');
            } 
        );
    }

    serverValidate(): void {
        console.log('server validate');
        this.passwordService.serverValidate( this.resetPasswordForm )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.commonSrvc.copyErrorsLeft(this.resetPasswordForm, data);
            },
            error => {
                console.log('error posting to reset-password-form-validate.json');
            } 
        );
    }

    validatePassword(): void {
        if( this.resetPasswordForm.retypedPassword != this.resetPasswordForm.password ){
            this.displayError = true;
        }
        this.displayError = false;
    }

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getResetPasswordForm();
    }; 
}