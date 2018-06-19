declare var orcidVar: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { AccountService } 
    from '../../shared/account.service.ts';


@Component({
    selector: 'security-question-edit-ng2',
    template:  scriptTmpl("security-question-edit-ng2-template")
})
export class SecurityQuestionEditComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    errors: any;
    initSecurityQuestionFlag: boolean;
    password: any;
    securityQuestions: any;
    securityQuestionPojo: any;
    showConfirmationWindow: any;

    constructor(
        private accountService: AccountService
    ) {
        this.errors = null;
        this.initSecurityQuestionFlag = false;
        this.password = null;
        this.securityQuestions = [];
        this.securityQuestionPojo = {
            securityQuestionId: null
        };
        this.showConfirmationWindow = false;

    }

    closeModal(): void {
        //$.colorbox.close();
    };

    checkCredentials(): void {
        if( orcidVar.isPasswordConfirmationRequired ){
            this.showConfirmationWindow = true;
            /*
            $.colorbox({
                html: $compile($('#check-password-modal').html())($scope)
            });
            */
        }
        else{
            this.submitModal();
        }
    };

    getSecurityQuestion(): void {
        this.accountService.getSecurityQuestion()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.securityQuestionPojo = data;
            },
            error => {
                //console.log('error with security question.json', error);
            } 
        );
    };

    initSecurityQuestion( obj ): void{

        if( this.initSecurityQuestionFlag == false ){
            this.initSecurityQuestionFlag = true;
            let objLastIndex = obj.length - 1;

            if(obj[objLastIndex] == ""){
                obj = obj.slice(0, -1);
            }

            this.securityQuestions = obj;
        }
    }

    submitModal(): void {
        this.securityQuestionPojo.password=this.password;
        this.accountService.submitModal( this.securityQuestionPojo )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
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
