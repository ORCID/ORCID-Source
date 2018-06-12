import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { TwoFAStateService } 
    from '../../shared/twoFAState.service.ts';



@Component({
    selector: 'two-fa-state-ng2',
    template:  scriptTmpl("two-fa-state-ng2-template")
})
export class TwoFAStateComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    showDisabled2FA: boolean;
    showEnabled2FA: boolean;

    constructor( 
        private twoFAStateService: TwoFAStateService,
    ) {
        this.showDisabled2FA = false;
        this.showEnabled2FA = false;
    }

    disable2FA(): void {
        this.twoFAStateService.disable()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                //console.log('this.getForm', data);
                this.update2FAStatus( data );

            },
            error => {
                //console.log('An error occurred disabling user 2FA', error);
            } 
        );
    };

    enable2FA(): void {
        window.location.href = getBaseUri() + '/2FA/setup';
    };

    update2FAStatus(status): void {
        this.showEnabled2FA = status.enabled;
        this.showDisabled2FA = !status.enabled;
        //$scope.$apply();
    };

    check2FAState(): void {
        this.twoFAStateService.checkState()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                //console.log('this.getForm', data);
                this.update2FAStatus( data );

            },
            error => {
                //console.log('getTwoFAStateFormError', error);
            } 
        );
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.check2FAState();
    };

}