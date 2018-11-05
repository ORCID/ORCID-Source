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
    selector: 'alt-signin-accounts-revoke-ng2',
    template:  scriptTmpl("alt-signin-accounts-revoke-ng2-template")
})
export class AltSigninAccountsRevokeComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    errors: any;
    password: any
    socialAccount: any;

    constructor(
        private accountService: AccountService,
        private modalService: ModalService
    ) {
        this.errors = [];

    }

    closeModal(): void {
        this.modalService.notifyOther({action:'close', moduleId: 'modalAltSigninAccountRevoke'});
    };

    revoke(): void {
        var revokeSocialAccount = {
            idToManage: null,
            password: null
        };
        revokeSocialAccount.idToManage = this.socialAccount.id;
        revokeSocialAccount.password = this.password;
        this.accountService.revokeSocialAccount( revokeSocialAccount )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(data.errors.length === 0){
                    this.accountService.notifyOther({action:'revoke', successful:true});
                    this.password = "";
                    this.closeModal();
                }
                else{
                    this.errors = data.errors;
                }
            },
            error => {
                //console.log('revokeSocialAccount', error);
            } 
        );
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.accountService.notifyObservable$.subscribe(
            (res) => {
                if( res.socialAccount ) {
                    this.socialAccount = res.socialAccount;
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
