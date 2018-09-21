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
    
@Component({
    selector: 'admin-actions-ng2',
    template:  scriptTmpl("admin-actions-ng2-template")
})
export class AdminActionsComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();    
   
    switchId: string;
    showSwitchUser: boolean;
    switchUserError: boolean;
    
    constructor(
        private switchUserService: SwitchUserService
    ) {
        this.showSwitchUser = false;
        this.switchUserError = false;
    }    

    switchUser(): void {
        this.switchUserService.adminSwitchUserValidate(this.switchId)
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
                console.log('admin: switchUser', error);
                this.switchUserError = true;
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
    }; 
}