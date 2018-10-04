declare var orcidGA: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { ClaimService } 
    from '../../shared/claim.service.ts'; 

import { CommonService } 
    from '../../shared/common.service.ts'; 
    
import { FeaturesService }
    from '../../shared/features.service.ts';

@Component({
    selector: 'claim-ng2',
    template:  scriptTmpl("claim-ng2-template")
})
export class ClaimComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();    

    postingClaim: boolean;
    claim: any;
    
    constructor(
        private claimService: ClaimService,
        private featuresService: FeaturesService,
        private commonService: CommonService
    ) {
        this.postingClaim = false;
        this.claim = { 'password': { 'value': '', 'errors':[] }, 'passwordConfirm': { 'value': '', 'errors':[] }, 'activitiesVisibilityDefault': {'visibility':'', 'errors':[]} };        
    }

    getClaim(): void{
        this.claimService.getClaim()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {                
                this.claim = data;
                this.claim.activitiesVisibilityDefault.visibility = '';
            },
            error => {
                console.log('getClaim', error);
            } 
        );
    };

    isValidClass(cur): string {
        let valid;
        if (cur === undefined) {
            return '';
        }
        valid = true;
        if (cur.required && (cur.value == null || cur.value.trim() == '')) {
            valid = false;
        }
        if (cur.errors !== undefined && cur.errors.length > 0) {
            valid = false;
        }
        return valid ? '' : 'text-error';
    };

    postClaim(): any {
        if (this.postingClaim) {
            return;
        }
        this.postingClaim = true;

        this.claimService.postClaim( this.claim )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.claim = data;

                if (this.claim.errors.length == 0) {
                    if (this.claim.url != null) {
                        orcidGA.gaPush(['send', 'event', 'RegGrowth', 'New-Registration', 'Website']);
                        orcidGA.windowLocationHrefDelay(this.claim.url);
                    }
                }
                this.postingClaim = false;
            },
            error => {
                this.postingClaim = false;
                console.log('postClaim', error);
            } 
        );
    };

    serverValidate(field): void {
        if (field === undefined) {
            field = '';
        }
        this.claimService.serverValidate( this.claim, field )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.commonService.copyErrorsLeft(this.claim, data);
            },
            error => {
                console.log("serverValidate", error);
            } 
        );
    };

    updateActivitiesVisibilityDefault(priv, $event): any {
        this.claim.activitiesVisibilityDefault.visibility = priv;
    };
    
    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getClaim();
    }; 
}