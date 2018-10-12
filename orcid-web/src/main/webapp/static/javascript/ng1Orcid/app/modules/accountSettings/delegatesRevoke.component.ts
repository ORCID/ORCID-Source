declare var $: any;
declare var delegateEmail: any;
declare var getBaseUri: any;
declare var isEmail: any;
declare var orcidVar: any;
declare var orcidSearchUrlJs: any;
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
    selector: 'delegates-revoke-ng2',
    template:  scriptTmpl("delegates-revoke-ng2-template")
})
export class DelegatesRevokeComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;
   
    isPasswordConfirmationRequired: any;
    errors: any;
    delegateToRevoke: any;
    delegateNameToRevoke: any;
    password: any;

    constructor(
        private accountService: AccountService,
        private modalService: ModalService
    ) {
        this.isPasswordConfirmationRequired = orcidVar.isPasswordConfirmationRequired;
        this.errors = [];
    }

    revoke(): void {

        var revokeDelegate = {
            delegateToManage: null,
            password: null
        };
        revokeDelegate.delegateToManage = this.delegateToRevoke;
        revokeDelegate.password = this.password;

        this.accountService.revoke( revokeDelegate )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(data.errors.length === 0){
                    this.accountService.notifyOther({action:'revoke', successful:true});
                    this.closeModal();
                }
                else{
                    this.errors = data.errors;
                }
            },
            error => {
                //console.log('setformDataError', error);
            } 
        );
    };

    closeModal(): void {
        this.modalService.notifyOther({action:'close', moduleId: 'modalRevokeDelegate'});
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directivesload
        this.subscription = this.accountService.notifyObservable$.subscribe(
            (res) => {
                if( res.delegateToRevoke ) {
                    this.delegateToRevoke = res.delegateToRevoke;
                }
                if( res.delegateNameToRevoke ) {
                    this.delegateNameToRevoke = res.delegateNameToRevoke;
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