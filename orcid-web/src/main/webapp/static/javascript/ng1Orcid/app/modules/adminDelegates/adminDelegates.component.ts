//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import 'rxjs/add/operator/takeUntil';

import { AdminDelegatesService } 
    from '../../shared/adminDelegates.service.ts'; 



@Component({
    selector: 'admin-delegates-ng2',
    template:  scriptTmpl("admin-delegates-ng2-template")
})
export class AdminDelegatesComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    managed_verified: boolean;
    request: any;
    showSection: boolean;
    success: boolean;
    trusted_verified: boolean;
   
    constructor(
        private adminDelegatesService: AdminDelegatesService
    ) {
        this.managed_verified = false;
        this.request = {
            trusted : {
                errors: [], 
                value: ''
            }, 
            managed : {
                errors: [], 
                value: ''
            }
        };
        this.showSection = false;
        this.success = false;
        this.trusted_verified = false;
    }

    checkClaimedStatus( whichField ): void {
        let orcidOrEmail = '';
        if(whichField == 'trusted') {
            this.trusted_verified = false;
            orcidOrEmail = this.request.trusted.value;
        } else {
            this.managed_verified = false;
            orcidOrEmail = this.request.managed.value;
        }

        this.adminDelegatesService.getFormData( orcidOrEmail )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                if(data) {
                    if(whichField == 'trusted') {
                        this.trusted_verified = true;
                    } else {
                        this.managed_verified = true;
                    }
                }
            },
            error => {
                //console.log('getformDataError', error);
            } 
        );
 
    };

    confirmDelegatesProcess(): void {
        this.success = false;
        this.adminDelegatesService.setFormData( this.request )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.request = data;
                if(data.successMessage) {
                    this.success = true;
                }
            },
            error => {
                //console.log('setformDataError', error);
            } 
        );
    };

    toggleSection(): void{
        this.showSection = !this.showSection;
        $('#delegates_section').toggle();
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
    }; 
}