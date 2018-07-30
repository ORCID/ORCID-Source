//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { CommonService }
    from '../../shared/common.service.ts';

import { ResendClaimService } 
    from '../../shared/resendClaim.service.ts'; 

@Component({
    selector: 'resend-claim-ng2',
    template:  scriptTmpl("resend-claim-ng2-template")
})
export class ResendClaimComponent implements AfterViewInit, OnDestroy {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    requestResendClaim: any;

    constructor(
        private commonSrvc: CommonService,
        private resendClaimService: ResendClaimService
    ) {
        var email = "";
        var urlParts = window.location.href.split('/');
        if(urlParts != null) {
            email = urlParts[urlParts.length -1];   
        }
        this.requestResendClaim = {
                "email": email,
                "errors": [],
                "successMessage": null
            }; 
    }

    postResendClaimRequest(): void {                
        this.resendClaimService.post( this.requestResendClaim )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.requestResendClaim = data;
            },
            error => {
                console.log('error on postResendClaimRequest');
            } 
        );
    }    

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };
}