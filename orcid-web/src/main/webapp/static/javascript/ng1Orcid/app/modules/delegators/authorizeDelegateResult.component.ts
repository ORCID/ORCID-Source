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
    from '../../shared/account.service'; 
    
@Component({
    selector: 'authorize-delegate-result-ng2',
    template:  scriptTmpl("authorize-delegate-result-ng2-template"),
})
export class AuthorizeDelegateResultComponent implements OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    delegate: string;
    invalidLink: boolean;
    wrongLink: boolean;

    constructor(
    ) {

    }

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        var delegateParam = window.location.href.split('delegate=')[1];
        if (delegateParam) {
            this.delegate = delegateParam;
        } else if (window.location.href.split('invalidToken=')[1]) {
            this.invalidLink = true;
        } else if (window.location.href.split('wrongToken=')[1]) {
            this.wrongLink = true;
        }        
    }; 
    
    getBaseUri(): String {
        return getBaseUri();
    };
}
