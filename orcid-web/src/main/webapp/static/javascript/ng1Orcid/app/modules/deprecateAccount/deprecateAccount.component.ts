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

import { GenericService } 
    from '../../shared/generic.service.ts'; 

import { ModalService } 
    from '../../shared/modal.service.ts'; 

import {EmailService } 
    from '../../shared/email.service.ts'; 

@Component({
    selector: 'deprecate-account-ng2',
    template:  scriptTmpl("deprecate-account-ng2-template")
})
export class DeprecateAccountComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    deprecateProfilePojo: any;
    elementHeight: any;
    elementWidth: any;

    constructor(
        private deprecateProfileService: GenericService,
        private emailService: EmailService,
        private accountService: AccountService,
        private modalService: ModalService
    ) {
        this.deprecateProfilePojo = {};
    }

    deprecateORCID = function() {
        this.accountService.deprecateORCID( this.deprecateProfilePojo )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.deprecateProfilePojo = data;
                if(data.errors.length == 0) {
                    this.openModal('modalDeprecateAccountConfirm', '645', '645');
                } 
            },
            error => {
                console.log('deprecateORCIDerror', error);
            } 
        );
    };

    getDeprecateProfile(): void {

        this.deprecateProfileService.getData( '/account/deprecate-profile.json' )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(data) {
                    this.deprecateProfilePojo = data;
                }
            },
            error => {
                console.log('getDeprecateAccountError', error);
            } 
        );
    }; 

    submitModal(): void {
        this.deprecateProfileService.setData( this.deprecateProfilePojo, '/account/confirm-deprecate-profile.json' )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(data) {
                    this.closeModal('modalDeprecateAccountConfirm');
                    this.openModal('modalDeprecateAccountSuccess', '400', '200');
                }
            },
            error => {
                console.log('submitDeprecateAccountError', error);
            } 
        );
    };
            
    openModal(id: string, width: string, height: string){
        this.elementWidth = width;
        this.elementHeight = height;
        this.deprecateProfileService.open(id);
    }

    cancelEditModal(id: string){
        this.deprecateProfilePojo = {};
        this.deprecateProfileService.close(id);
    } 

    closeModal(id: string){
        this.deprecateProfileService.close(id);
    } 

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getDeprecateProfile();
    }; 
}