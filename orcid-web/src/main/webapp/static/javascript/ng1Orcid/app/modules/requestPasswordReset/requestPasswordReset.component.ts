declare var $: any; //delete
declare var orcidVar: any;
declare var logAjaxError: any;
declare var getBaseUri: any;
declare var om: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, ChangeDetectorRef, ElementRef, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

import { CommonService } 
    from '../../shared/common.service.ts';

import { RequestPasswordResetService } 
    from '../../shared/requestPasswordReset.service.ts';


@Component({
    selector: 'request-password-reset-ng2',
    template:  scriptTmpl("request-password-reset-ng2-template")
})
export class RequestPasswordResetComponent implements AfterViewInit, OnDestroy, OnInit {
    
    @Input() authorizationForm : any;
    @Input() showDeactivatedError: any;
    @Input() showReactivationSent: any;
    @Output() sendReactivationEmail: EventEmitter<any> = new EventEmitter<any>();

    private ngUnsubscribe: Subject<void> = new Subject<void>();
    showResetPassword: any;
    resetPasswordToggleText: any;
    requestResetPassword: any;
    showSendResetLinkError: any;

    constructor(
        private cdr:ChangeDetectorRef,
        private commonService: CommonService,
        private elementRef: ElementRef, 
        private requestPasswordResetService: RequestPasswordResetService,
    ) {
        this.authorizationForm = elementRef.nativeElement.getAttribute('authorizationForm');
        this.showDeactivatedError = elementRef.nativeElement.getAttribute('showDeactivatedError');
        this.showReactivationSent = elementRef.nativeElement.getAttribute('showReactivationSent');
        this.showSendResetLinkError = false;
        this.requestResetPassword = {};
    }

    getRequestResetPassword(): void {
        this.requestPasswordResetService.getResetPasswordRequest( )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.requestResetPassword = data;
            },
            error => {
                console.log("error getting reset-password.json");
            } 
        );
    };

    postPasswordResetRequest(): void {
        this.requestResetPassword.successMessage = null;
        this.requestResetPassword.errors = null;
        this.showSendResetLinkError = false;
        this.showDeactivatedError = false;

        this.requestPasswordResetService.postResetPasswordRequest( this.requestResetPassword )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.requestResetPassword = data; 
                this.showDeactivatedError = ($.inArray('orcid.frontend.security.orcid_deactivated', this.requestResetPassword.errors) != -1);               
                this.cdr.detectChanges();
            },
            error => {
                this.requestResetPassword.errors = null;
                this.showSendResetLinkError = true;
                console.log("error posting to /reset-password.json");
                this.cdr.detectChanges();
            } 
        );

    };

    sendReactivation(email?): void {
        let _email = email;
        this.sendReactivationEmail.emit(_email);
    }

    toggleResetPassword(): void {
        this.showResetPassword = !this.showResetPassword;

        // pre-populate with email from signin form 
        if (typeof this.authorizationForm != "undefined" 
            && this.authorizationForm.userName
            && this.commonService.isEmail(this.authorizationForm.userName.value)
        ) {
            this.requestResetPassword = {
                email:  this.authorizationForm.userName.value
            } 
        } else {
            this.requestResetPassword = {
                email:  ""
            }
        }
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
        this.getRequestResetPassword();
        // init reset password toggle text
        this.showSendResetLinkError = false;
        this.showResetPassword = (window.location.hash === "#resetPassword");
        this.resetPasswordToggleText = om.get("login.forgotten_password");
          
    }; 
}