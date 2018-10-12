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

import { ModalService } 
    from '../../shared/modal.service.ts';

@Component({
    selector: 'trusted-organizations-ng2',
    template:  scriptTmpl("trusted-organizations-ng2-template")
})

export class TrustedOrganizationsComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    applicationSummary: any;
    applicationSummaryList: any;

    constructor(
        private accountService: AccountService,
        private modalService: ModalService
    ) {
        this.applicationSummary = null;
        this.applicationSummaryList = null;
    }

    confirmRevoke(applicationSummary): void {
        this.applicationSummary = applicationSummary;
        this.accountService.notifyOther({applicationSummary:this.applicationSummary});
        this.modalService.notifyOther({action:'open', moduleId: 'modalTrustedOrganizationsRevoke'});
    };

    getApplications(): void {
        this.accountService.getTrustedOrgs()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.applicationSummaryList = data;  
            },
            error => {
                //console.log('error getting trusted orgs', error);
            } 
        );
    };

    getApplicationUrlLink(application): string {
        if(application.websiteValue != null) {
            if(application.websiteValue.lastIndexOf('http://') === -1 
                && application.websiteValue.lastIndexOf('https://') === -1) {
                return '//' + application.websiteValue;
            } else {
                return application.websiteValue;
            }
        }
        return '';
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directivesload
        this.subscription = this.accountService.notifyObservable$.subscribe(
            (res) => {                
                if(res.action == 'revoke') {
                    if(res.successful == true) {
                        this.getApplications();
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
        this.getApplications();
    }; 

}