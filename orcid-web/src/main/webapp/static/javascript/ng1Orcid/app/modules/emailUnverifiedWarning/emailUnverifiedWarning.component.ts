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

import { EmailService } 
    from '../../shared/email.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

@Component({
    selector: 'email-unverified-warning-ng2',
    template:  scriptTmpl("email-unverified-warning-ng2-template")
})
export class EmailUnverifiedWarningComponent implements AfterViewInit, OnDestroy, OnInit {

    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private viewSubscription: Subscription;

    emailPrimary: string;
    delayVerifyEmailAlert: boolean;

    constructor(
        private accountService: AccountService,
        private emailService: EmailService,
        private modalService: ModalService
    ) {
        this.emailPrimary = '';
        this.delayVerifyEmailAlert = false;
    }

    close(): void {
        if(this.delayVerifyEmailAlert == true){
            this.accountService.delayVerifyEmail()
            .pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
     
                },
                error => {
                    //console.log('getWebsitesFormError', error);
                } 
            );
        }
        this.modalService.notifyOther({action:'close', moduleId: 'modalemailunverified'});
    }

    getEmails(): any {
        this.emailService.getEmails()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.emailPrimary = this.emailService.getEmailPrimary().value;
            },
            error => {
                //console.log('getEmails', error);
            } 
        );
    }

    verifyEmail(): any {
        this.emailService.verifyEmail()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                ////console.log('verifyEmail', data);
            },
            error => {
                //console.log('verifyEmail', error);
            } 
        );
        this.modalService.notifyOther({action:'close', moduleId: 'modalemailunverified'});
        this.modalService.notifyOther({action:'open', moduleId: 'emailSentConfirmation'});
    }

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.viewSubscription = this.modalService.notifyObservable$.subscribe(
            (res) => {
                if(res.moduleId == "modalemailunverified") {
                    if(res.action == "open" && res.delay == true) {
                        this.delayVerifyEmailAlert = true;
                    }
                    if(res.primaryEmail) {
                        this.emailPrimary = res.primaryEmail;
                    } else {
                        this.getEmails();
                    }
                }
            }
        );
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        
    };
}