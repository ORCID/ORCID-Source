import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, ElementRef, Input, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';

import { takeUntil } 
    from 'rxjs/operators';

import { EmailService } 
    from '../../shared/email.service.ts';

import { FeaturesService } 
    from '../../shared/features.service.ts';

import { GenericService } 
    from '../../shared/generic.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

@Component({
    selector: 'my-orcid-alerts-ng2',
    template:  scriptTmpl("my-orcid-alerts-ng2-template")
})
export class MyOrcidAlertsComponent implements AfterViewInit, OnDestroy, OnInit {
    @Input() checkEmailValidated: any;
    @Input() inDelegationMode: any;

    private ngUnsubscribe: Subject<void> = new Subject<void>();

    elementHeight: any;
    elementWidth: any;
    primaryEmail: string;
    sourceGrantReadWizard: any;
    manualEditVerificationFeatureEnabled = this.featuresService.isFeatureEnabled('EMAIL_VERIFICATION_MANUAL_EDIT');

    constructor(
        private elementRef: ElementRef,
        private emailService: EmailService,
        private featuresService: FeaturesService,
        private genericService: GenericService,
        private modalService: ModalService
    ) {
        this.checkEmailValidated = elementRef.nativeElement.getAttribute('checkEmailValidated');
        this.elementHeight = "120";
        this.elementWidth = "500";
        this.primaryEmail = '';
        this.inDelegationMode = elementRef.nativeElement.getAttribute('inDelegationMode');
    }

    close(id: string): void {
        this.genericService.close(id);
    }

    checkPrimaryEmailVerified(): any {
        this.elementHeight="260";
        this.emailService.getEmails()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.primaryEmail = this.emailService.getEmailPrimary().value;
                if( !this.emailService.getEmailPrimary().verified ){
                    this.modalService.notifyOther({action:'open', moduleId: 'modalemailunverified', delay: true, primaryEmail:this.primaryEmail});
                }
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
        } else if ((!this.checkEmailValidated || this.checkEmailValidated=="false") && (!this.inDelegationMode || this.inDelegationMode=="false")){
            this.checkPrimaryEmailVerified();
        }
    };
}