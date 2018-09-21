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
    
    constructor(
        private switchUserService: SwitchUserService
    ) {
        this.showSwitchUser = false;
        console.log("Hey!" + this.showSwitchUser)
    }    

    switchUser(): void {
        this.switchUserService.switchUserAdmin(this.switchId)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(!$.isEmptyObject(data)) {
                    if($.isEmptyObject(data.errorMessg)) { 
                        this.switchUserService.switchUser(this.switchId);
                    }
                }
            },
            error => {
                console.log('admin: switchUser', error);
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