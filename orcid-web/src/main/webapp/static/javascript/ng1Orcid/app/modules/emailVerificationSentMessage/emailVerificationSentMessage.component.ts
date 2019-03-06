import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { EmailService } 
    from '../../shared/email.service';

import { ModalService } 
    from '../../shared/modal.service'; 

@Component({
    selector: 'email-verification-sent-messsage-ng2',
    template:  scriptTmpl("email-verification-sent-messsage-ng2-template")
})
export class EmailVerificationSentMesssageComponent implements AfterViewInit, OnDestroy, OnInit {

    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    emailPrimary: string;

    constructor(
        private emailService: EmailService,
        private modalService: ModalService
    ) {
        this.emailPrimary = '';
    }

    close(): void {
        this.modalService.notifyOther({action:'close', moduleId: 'emailSentConfirmation'});
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

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {

        this.subscription = this.modalService.notifyObservable$.subscribe(
            (res: any) => {
                if ( res.moduleId == "emailSentConfirmation" ) {
                    if ( res.data != undefined) {
                        this.emailPrimary = res.data.email;
                    }
                    else {
                        this.getEmails();
                    }

                }
            }
        );
    };
}