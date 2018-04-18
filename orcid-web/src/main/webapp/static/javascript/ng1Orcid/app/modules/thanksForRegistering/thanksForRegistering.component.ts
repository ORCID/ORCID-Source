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

import { EmailService } 
    from '../../shared/email.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

@Component({
    selector: 'thanks-for-registering-ng2',
    template:  scriptTmpl("thanks-for-registering-ng2-template")
})
export class ThanksForRegisteringComponent implements AfterViewInit, OnDestroy, OnInit {

    private ngUnsubscribe: Subject<void> = new Subject<void>();

    emailPrimary: string;

    constructor(
        private emailService: EmailService,
        private modalService: ModalService
    ) {
        this.emailPrimary = '';
    }

    close(): void {
        this.modalService.notifyOther({action:'close', moduleId: 'modalemailunverified'});
    }

    getEmails(): any {
        this.emailService.getEmails()
        .takeUntil(this.ngUnsubscribe)
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
        .takeUntil(this.ngUnsubscribe)
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
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getEmails();
    };
}