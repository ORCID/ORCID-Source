declare var orcidVar: any;

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
   
    delegateNameToRevoke: any;
    delegateToRevoke: any;
    errors: any;
    isPasswordConfirmationRequired: any;
    password: any;

    constructor(
        private accountService: AccountService,
        private modalService: ModalService
    ) {
        this.errors = [];
        this.isPasswordConfirmationRequired = orcidVar.isPasswordConfirmationRequired;
    }

    closeModal(): void {
        this.modalService.notifyOther({action:'close', moduleId: 'modalRevokeDelegate'});
    };

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
    
    getBaseUri() : String {
        return getBaseUri();
    };
}