import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';

import { takeUntil } 
    from 'rxjs/operators';

import { EmailService } 
    from '../../shared/email.service.ts';

import { GenericService } 
    from '../../shared/generic.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

@Component({
    selector: 'my-orcid-alerts-ng2',
    template:  scriptTmpl("my-orcid-alerts-ng2-template")
})
export class MyOrcidAlertsComponent implements AfterViewInit, OnDestroy, OnInit {

    private ngUnsubscribe: Subject<void> = new Subject<void>();

    elementHeight: any;
    elementWidth: any;
    emailPrimary: string;
    sourceGrantReadWizard: any;

    constructor(
        private emailService: EmailService,
        private genericService: GenericService,
        private modalService: ModalService
    ) {
        this.elementHeight = "120";
        this.elementWidth = "500";
        this.emailPrimary = '';
    }

    close(id: string): void {
        this.genericService.close(id);
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

    getSourceGrantReadWizard(): any {
        this.genericService.getData('/my-orcid/sourceGrantReadWizard.json')
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.sourceGrantReadWizard = data;
                if(this.sourceGrantReadWizard.url){
                    this.elementHeight = "160";
                }
                this.genericService.open('claimed-record-thanks');
            },
            error => {
                //console.log('getEmails', error);
            } 
        );
    };

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

    yes(): any {
        this.close('claimed-record-thanks');
        var newWin = window.open(this.sourceGrantReadWizard.url);
        if (!newWin) {
            window.location.href = this.sourceGrantReadWizard.url;
        }
        else {
            newWin.focus();
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
        var urlParams = new URLSearchParams(window.location.search);
        if(urlParams.has('recordClaimed')){
            this.getSourceGrantReadWizard()
        }
        this.getEmails();
    };
}