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

import { ModalService } 
    from '../../shared/modal.service'; 

@Component({
    selector: 'trusted-organizations-revoke-ng2',
    template:  scriptTmpl("trusted-organizations-revoke-ng2-template")
})
export class TrustedOrganizationsRevokeComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    applicationSummary: any;

    constructor(
        private accountService: AccountService,
        private modalService: ModalService
    ) {


    }

    closeModal(): void {
        this.modalService.notifyOther({action:'close', moduleId: 'modalTrustedOrganizationsRevoke'});
    };

    revokeAccess(): void{
        this.accountService.revokeTrustedOrg(this.applicationSummary)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.accountService.notifyOther({action:'revoke', successful:true});
                this.closeModal(); 

            },
            error => {
                //console.log('error revoking trusted org', error);
            } 
        );
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.accountService.notifyObservable$.subscribe(
            (res) => {
                if( res.applicationSummary ) {
                    this.applicationSummary = res.applicationSummary;
                }
            }
        );
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
    }; 
}
