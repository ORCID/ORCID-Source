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
    selector: 'trusted-organizations-ng2',
    template:  scriptTmpl("trusted-organizations-ng2-template")
})

export class TrustedOrganizationsComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    applicationSummary: any;
    applicationSummaryList: any;

    constructor(
        private accountService: AccountService
    ) {
        this.applicationSummary = null;
        this.applicationSummaryList = null;
    }

    closeModal(): void {
        //$.colorbox.close();
    };

    confirmRevoke(applicationSummary): void {
        this.applicationSummary = applicationSummary;
        /*$.colorbox({
            html : $compile($('#confirm-revoke-access-modal').html())(this),
            transition: 'fade',
            close: '',
            onLoad: function() {
                $('#cboxClose').remove();
            },
            onComplete: function() {$.colorbox.resize();},
            scrolling: true
        });*/
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

    revokeAccess(): void{
        this.accountService.revokeTrustedOrg(this.applicationSummary)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.getApplications();
                this.closeModal(); 
            },
            error => {
                //console.log('error revoking trusted org', error);
            } 
        );
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
        this.getApplications();
    }; 

}