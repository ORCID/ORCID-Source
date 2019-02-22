declare var $: any;

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
    selector: 'authorize-delegate-result-ng2',
    template:  scriptTmpl("authorize-delegate-result-ng2-template"),
})
export class AuthorizeDelegateResultComponent implements OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    authorizeDelegateResult: any;

    constructor(
         private accountService: AccountService
    ) {

    }

    getAuthorizeDelegateResult(): void {
        var key = window.location.href.split('key=')[1];
        this.accountService.getAuthorizeDelegateResult(key)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        ).subscribe(
            data => {
                this.authorizeDelegateResult = data;
            },
            error => {
                console.log('error fetching authorize delegate result', error);
            } 
        );
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getAuthorizeDelegateResult();
    }; 
    
    getBaseUri(): String {
        return getBaseUri();
    };
}
