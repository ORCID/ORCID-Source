//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';

import { takeUntil } 
    from 'rxjs/operators';

import { SwitchUserService } 
    from '../../shared/switchUser.service.ts';
    
import { AdminDelegatesService } 
    from '../../shared/adminDelegates.service.ts'; 
    
@Component({
    selector: 'admin-actions-ng2',
    template:  scriptTmpl("admin-actions-ng2-template")
})
export class AdminActionsComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();    
   
    // Switch user
    switchId: string;
    showSwitchUser: boolean;
    switchUserError: boolean;
    
    // Find ids
    csvEmails: string;
    profileList: any;
    showFindIds: boolean;
    showIds: boolean;
    
    // Reset password
    showResetPassword: boolean;
    resetPasswordParams: any;
    
    constructor(
        private switchUserService: SwitchUserService,
        private adminDelegatesService: AdminDelegatesService
    ) {
        this.showSwitchUser = false;
        this.switchUserError = false;
        
        this.csvEmails = '';
        this.showFindIds = false;
        this.showIds = false;
        this.profileList = {};
    
        showResetPassword = false;
        resetPasswordParams = {};
    }    

    switchUser(id): void {
        this.switchUserService.adminSwitchUserValidate(id)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {                
                if(data != null && data.errorMessg == null) {
                    this.switchUserError = false;
                    window.location.replace(getBaseUri() + '/switch-user?username=' + data.id);                    
                } else {
                    this.switchUserError = true;
                }
            },
            error => {
                console.log('admin: switchUser error', error);
                this.switchUserError = true;
            } 
        );
        
    };
    
    findIds(): void {
        console.log(this.csvEmails)
        this.adminDelegatesService.findIds( this.csvEmails )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.showIds = true;
                if(data) {
                    this.profileList = data;
                } else {
                    this.profileList = {};
                }                
            },
            error => {
                console.log('admin: findIds error', error);
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

    ngOnInit() {
    }; 
}