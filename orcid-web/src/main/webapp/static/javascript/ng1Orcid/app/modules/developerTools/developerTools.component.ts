declare var orcidVar: any;

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit, EventEmitter, Output } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
    
import { takeUntil } 
    from 'rxjs/operators';

import { OauthService } 
    from '../../shared/oauth.service.ts'; 

import { CommonService } 
    from '../../shared/common.service.ts';

import { DeveloperToolsService } 
    from '../../shared/developerTools.service.ts'; 
    
import { EmailService } 
    from '../../shared/email.service.ts';

@Component({
    selector: 'developer-tools-ng2',
    template:  scriptTmpl("developerTools-ng2-template")
})
export class DeveloperToolsComponent implements AfterViewInit, OnDestroy, OnInit {    
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    developerToolsEnabled: boolean;
    client: any;
    showTerms: boolean;
    acceptedTerms: boolean;
    verifyEmailSent: boolean;
    
    constructor(
            private commonSrvc: CommonService,
            private developerToolsService: DeveloperToolsService,
            private emailService: EmailService,
            private cdr:ChangeDetectorRef
        ) {
        this.client = {};
        this.showTerms = false;
        this.acceptedTerms = false;
        this.verifyEmailSent = false;
        this.developerToolsEnabled = orcidVar.developerToolsEnabled;
    }
    
    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    verifyEmail(primaryEmail): void {
        console.log('Primary email: ' + primaryEmail);
        this.emailService.verifyEmail(primaryEmail)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                console.log(primaryEmail + ' verify email sent');
                this.verifyEmailSent = true;
            },
            error => {
                console.log('verifyEmail Error', error);
            } 
        );
    };
    
    getClient(): void {
        this.developerToolsService.getClient()
        .pipe(    
                takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.client = data;                    
            },
            error => {
                console.log("error ngOnInit", error);
            } 
        );
    };
    
    enableDeveloperTools(): void {
        if(this.acceptedTerms){
            this.developerToolsService.enableDeveloperTools()
            .pipe(    
                    takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    if(data) {
                        this.developerToolsEnabled = true;
                        this.getClient();
                    }                    
                },
                error => {
                    console.log("error ngOnInit", error);
                } 
            );
        }
    }
    
    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };
    
    ngOnInit() {
        this.getClient();
    };
}