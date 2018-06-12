declare var orcidGA: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

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
    register: any;

    constructor(
        private claimService: ClaimService,
        private featuresService: FeaturesService,
        private commonService: CommonService
    ) {
        this.postingClaim = false;
        this.register = {};
    }

    getClaim(): void{
        this.claimService.getClaim()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.register = data;
                //console.log('this.registerData', this.register);
            },
            error => {
                //console.log('getregisterDataError', error);
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

        this.claimService.postClaim( this.register )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.register = data;

                if (this.register.errors.length == 0) {
                    if (this.register.url != null) {
                        orcidGA.gaPush(['send', 'event', 'RegGrowth', 'New-Registration', 'Website']);
                        orcidGA.windowLocationHrefDelay(this.register.url);
                    }
                }
                this.postingClaim = false;
            },
            error => {
                this.postingClaim = false;
                //console.log('setformDataError', error);
            } 
        );
    };

    serverValidate(field): void {
        if (field === undefined) {
            field = '';
        }
        this.claimService.serverValidate( this.register, field )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.commonService.copyErrorsLeft(this.register, data);
            },
            error => {
                //console.log("serverValidate() error", error);
            } 
        );
    };

    updateActivitiesVisibilityDefault(priv, $event): any {
        this.register.activitiesVisibilityDefault.visibility = priv;
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