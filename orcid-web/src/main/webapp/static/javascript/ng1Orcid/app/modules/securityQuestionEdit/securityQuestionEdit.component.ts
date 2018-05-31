declare var orcidVar: any;

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

import { AccountService } 
    from '../../shared/account.service.ts';


@Component({
    selector: 'security-question-edit-ng2',
    template:  scriptTmpl("security-question-edit-ng2-template")
})
export class SecurityQuestionEditComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    errors: any;
    password: any;
    securityQuestions: any;
    securityQuestionPojo: any;

    constructor(
        private accountService: AccountService
    ) {
        this.errors = null;
        this.password = null;
        this.securityQuestions = [];
        this.securityQuestionPojo = {
            securityQuestionId: null
        };

    }

    closeModal(): void {
        //$.colorbox.close();
    };

    checkCredentials(): void {
        this.password = null;
        if( orcidVar.isPasswordConfirmationRequired ){
            /*
            $.colorbox({
                html: $compile($('#check-password-modal').html())($scope)
            });
            $.colorbox.resize();
            */
        }
        else{
            this.submitModal();
        }
    };

    getSecurityQuestion(): void {
        this.accountService.getSecurityQuestion()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.securityQuestionPojo = data;
            },
            error => {
                //console.log('error with security question.json', error);
            } 
        );
    };

    submitModal(): void {
        this.accountService.submitModal( this.securityQuestionPojo )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                if(data.errors.length != 0) {
                    this.errors=data.errors;
                } else {
                    this.errors=null;
                }
                this.getSecurityQuestion();
            },
            error => {
                //console.log('error with security question', error);
            } 
        );
        this.password=null;
        this.closeModal();
        
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
        this.getSecurityQuestion();
    }; 
}
