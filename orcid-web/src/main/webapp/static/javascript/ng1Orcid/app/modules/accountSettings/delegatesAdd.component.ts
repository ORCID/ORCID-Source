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
    selector: 'delegates-add-ng2',
    template:  scriptTmpl("delegates-add-ng2-template")
})
export class DelegatesAddComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;
   
    effectiveUserOrcid: any;
    input: any;
    realUserOrcid: any;
    isPasswordConfirmationRequired: any;
    emailSearchResult: any;
    errors: any;
    delegateIdx: any;
    delegateToAdd: any;
    delegateNameToAdd: any;
    password: any;

    constructor(
        private accountService: AccountService,
        private modalService: ModalService
    ) {
        this.effectiveUserOrcid = orcidVar.orcidId;
        this.isPasswordConfirmationRequired = orcidVar.isPasswordConfirmationRequired;
        this.errors = [];
    }

    addDelegate(): void {
        var addDelegate = {
            delegateToManage: null,
            password: null
        };
        addDelegate.delegateToManage = this.delegateToAdd;
        addDelegate.password = this.password;

        this.accountService.addDelegate( addDelegate )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(data.errors.length === 0){
                    this.accountService.notifyOther({action:'add', successful:true});
                    //this.results.splice(this.delegateIdx, 1);
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

    addDelegateByEmail(): void {

        var addDelegate = {
            delegateEmail: null,
            password: null
        };
        
        this.errors = [];
        
        addDelegate.delegateEmail = this.input.text;
        addDelegate.password = this.password;

        this.accountService.addDelegateByEmail( addDelegate )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(data.errors.length === 0){
                    this.accountService.notifyOther({action:'add', successful:true});
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
        this.modalService.notifyOther({action:'close', moduleId: 'modalAddDelegate'});
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directivesload
        this.subscription = this.accountService.notifyObservable$.subscribe(
            (res) => {
                console.log(res);
                if( res.emailSearchResult ) {
                    this.emailSearchResult = res.emailSearchResult;
                }
                if( res.input ) {
                    this.input = res.input;
                }
                if( res.delegateNameToAdd ) {
                    this.delegateNameToAdd = res.delegateNameToAdd;
                }
                if( res.delegateToAdd ) {
                    this.delegateToAdd = res.delegateToAdd;
                }
                if( res.delegateIdx ) {
                    this.delegateIdx = res.delegateIdx;
                }
            }
        );
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        console.log(this.effectiveUserOrcid);
    }; 
}