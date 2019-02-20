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
    
import { Router, ActivatedRoute, Params }
    from '@angular/router';

@Component({
    selector: 'authorize-delegate-result-ng2',
    template:  scriptTmpl("authorize-delegate-result-ng2-template"),
})
export class AuthorizeDelegateResultComponent implements OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    authorizeDelegateResult: any;

    constructor(
         private accountService: AccountService,
         private activatedRoute: ActivatedRoute
    ) {

    }

    getAuthorizeDelegateResult(): void {
    console.log(this.activatedRoute.snapshot);
        var key = this.activatedRoute.snapshot.queryParamMap.get('key');
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
}
