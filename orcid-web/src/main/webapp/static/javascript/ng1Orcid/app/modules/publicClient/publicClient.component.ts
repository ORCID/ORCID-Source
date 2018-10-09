import { NgForOf, NgIf } 
    from '@angular/common'; 

import { ChangeDetectorRef, Component, OnDestroy, OnInit, EventEmitter, Output } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
    
import { takeUntil } 
    from 'rxjs/operators';

import { OauthService } 
    from '../../shared/oauth.service.ts'; 

import { CommonService } 
    from '../../shared/common.service.ts';

import { PublicClientService } 
    from '../../shared/publicClient.service.ts'; 

@Component({
    selector: 'publicClient-ng2',
    template:  scriptTmpl("publicClient-ng2-template")
})
export class PublicClientComponent implements OnDestroy, OnInit {    
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    constructor(
            private commonSrvc: CommonService,
            private publicClientService: PublicClientService,
            private cdr:ChangeDetectorRef
        ) {
        
    }
    
    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        
    };
}