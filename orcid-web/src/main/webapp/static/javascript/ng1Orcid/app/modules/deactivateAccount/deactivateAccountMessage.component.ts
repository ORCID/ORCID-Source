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

import { EmailService }
    from '../../shared/email.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts';

@Component({
    selector: 'deactivate-account-message-ng2',
    template:  scriptTmpl("deactivate-account-message-ng2-template")
})
export class DeactivateAccountMessageComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
   
    primaryEmail: string;

    constructor(
        private accountService: AccountService,
        private emailService: EmailService,
        private modalService: ModalService
    ) {
        this.primaryEmail = "";
    }

    closeModal(): void {
        this.modalService.notifyOther({action:'close', moduleId: 'modalDeactivateAccountMessage'});
    };

    getEmails(): any {
        this.emailService.getEmails()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.primaryEmail = this.emailService.getEmailPrimary().value;
            },
            error => {
                //console.log('getEmails', error);
            } 
        );
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