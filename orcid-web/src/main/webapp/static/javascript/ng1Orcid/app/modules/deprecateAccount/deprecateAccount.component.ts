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

@Component({
    selector: 'deprecate-account-ng2',
    template:  scriptTmpl("deprecate-account-ng2-template")
})
export class DeprecateAccountComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    deprecateProfilePojo: any;
    url_path: string;

    constructor(
        private deprecateProfileService: GenericService,
        private accountService: AccountService,
        private modalService: ModalService
    ) {
        this.deprecateProfilePojo = {};
        this.url_path = '/account/deprecate-profile.json';
    }

    deprecateORCID = function() {
        this.accountService.deprecateORCID( this.deprecateProfilePojo )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(data.errors.length == 0) {
                    this.deprecateProfileSevice.setStoredData( "deprecateProfile", this.deprecateProfilePojo );
                    this.modalService.notifyOther({action:'open', moduleId: 'deprecateAccountModal', edit: false});
                }
            },
            error => {
                //console.log('getformDataError', error);
            } 
        );
    };

    getDeprecateProfile(): void {

        this.deprecateProfileService.getData( this.url_path )
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
                //console.log('getformDataError', error);
            } 
        );
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
        this.getDeprecateProfile();
    }; 
}


@Component({
    selector: 'deprecate-account-modal-ng2',
    template:  scriptTmpl("deprecate-account-modal-ng2-template")
})
export class DeprecateAccountModalComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    deprecateProfilePojo: any;
    url_path: string;

    constructor(
        private deprecateProfileService: GenericService,
        private accountService: AccountService,
        private modalService: ModalService
    ) {
        this.deprecateProfilePojo = {};
        this.url_path = '/account/deprecate-profile.json';
    }

    closeModal(): void{
        this.modalService.notifyOther({action:'close', moduleId: 'deprecateAccountModal'});
    };

    getDeprecateProfile(): void {

        this.deprecateProfileService.getStoredData( "deprecateProfile" )
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
                //console.log('getformDataError', error);
            } 
        );
    };

    submitModal(): void {
        this.deprecateProfileService.setData( this.deprecateProfilePojo, this.url_path )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(data) {
                    this.closeModal();
                    /*
                        $rootScope.$broadcast('rebuildEmails', emailData);

                    */
                }
            },
            error => {
                //console.log('getformDataError', error);
            } 
        );
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
        this.getDeprecateProfile();
    }; 
}