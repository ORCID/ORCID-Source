//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { CommonService }
    from '../../shared/common.service.ts';

import { UnsubscribeService } 
    from '../../shared/unsubscribe.service.ts'; 


@Component({
    selector: 'unsubscribe-ng2',
    template:  scriptTmpl("unsubscribe-ng2-template")
})
export class UnsubscribeComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    displayError: boolean;
    notificationSettingsForm: any;

    constructor(
        private commonSrvc: CommonService,
        private unsubscribeService: UnsubscribeService
    ) {
        console.log('Constructor');               
        this.displayError = false;
        this.notificationSettingsForm = {};
    }

    getNotificationSettingsForm(): void {
        this.unsubscribeService.getNotificationSettingsForm()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.notificationSettingsForm = data;
            },
            error => {
                console.log('error fetching notification settings');
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
        console.log('Init')
        this.getNotificationSettingsForm();
        console.log('Done')       
    };
}