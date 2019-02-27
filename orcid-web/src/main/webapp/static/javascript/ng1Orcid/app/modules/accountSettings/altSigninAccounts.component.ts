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
    from '../../shared/account.service'; 

import { DiscoService } 
    from '../../shared/disco.service'; 

import { ModalService } 
    from '../../shared/modal.service';

import { WidgetService } 
    from '../../shared/widget.service'; 

@Component({
    selector: 'alt-signin-accounts-ng2',
    template:  scriptTmpl("alt-signin-accounts-ng2-template")
})

export class AltSigninAccountsComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    feed: any;
    isPasswordConfirmationRequired: boolean;
    socialAccount: any;
    socialAccounts: any;
    sort: any;

    constructor(
        private accountService: AccountService,
        private discoService: DiscoService,
        private modalService: ModalService,
        private widgetService: WidgetService
    ) {
        this.isPasswordConfirmationRequired = orcidVar.isPasswordConfirmationRequired;
        this.sort = {
                column: 'providerUserId',
                descending: false
            };
    }

    changeSorting(column): void{
        var sort = this.sort;
        if (sort.column === column) {
            sort.descending = !sort.descending;
        } else {
            sort.column = column;
            sort.descending = false;
        }
    };

    confirmRevoke(socialAccount): void {
        this.socialAccount = socialAccount;
        this.accountService.notifyOther({socialAccount:this.socialAccount});
        this.modalService.notifyOther({action:'open', moduleId: 'modalAltSigninAccountRevoke'});
    };

    getSocialAccounts(): void {
        console.log("getting social accounts");
        this.accountService.getSocialAccounts()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.socialAccounts = data;  
                this.populateIdPNames();
            },
            error => {
                //console.log('error getting trusted orgs', error);
            } 
        );
    };

    loadDiscoFeed = function() {
        this.discoService.getDiscoFeed()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.feed = data;
            },
            error => {
                console.log('Error getting disco feed');
            } 
        );
    };

    populateIdPNames(): void {
        var account = null;
        var name = null;
        for(var i in this.socialAccounts){
            account = this.socialAccounts[i];
            if(account.id.providerid === "facebook" || account.id.providerid === "google"){
                account.idpName = account.id.providerid.charAt(0).toUpperCase() + account.id.providerid.slice(1);
            } else if(this.feed) {
                account.idpName = this.discoService.getIdpName(account.id.providerid, this.feed, this.widgetService.getLocale());
            } else {
                account.idpName = "";
            } 
        }
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directivesload
        this.subscription = this.accountService.notifyObservable$.subscribe(
            (res) => {                
                if(res.action == 'revoke') {
                    if(res.successful == true) {
                        this.getSocialAccounts();
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
        this.getSocialAccounts();
        this.loadDiscoFeed();
    }; 

}