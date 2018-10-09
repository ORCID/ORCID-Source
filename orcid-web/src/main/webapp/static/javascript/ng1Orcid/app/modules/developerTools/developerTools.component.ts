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

import { DeveloperToolsService } 
    from '../../shared/developerTools.service.ts'; 

@Component({
    selector: 'developerTools-ng2',
    template:  scriptTmpl("developerTools-ng2-template")
})
export class DeveloperToolsComponent implements OnDestroy, OnInit {    
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    client: any;
    
    constructor(
            private commonSrvc: CommonService,
            private developerToolsService: DeveloperToolsService,
            private cdr:ChangeDetectorRef
        ) {
        this.client = {};
    }
    
    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    getClient(): void {
        this.developerToolsService.getClient()
        .pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    this.client = data;                    
                },
                error => {
                    console.log("error ngOnInit", error);
                } 
            );
    }
    
    ngOnInit() {
        this.getClient();
    };
}