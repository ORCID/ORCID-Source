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
    sendQuarterlyTips: boolean;

    constructor(
        private commonSrvc: CommonService,
        private unsubscribeService: UnsubscribeService
    ) {              
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
                this.sendQuarterlyTips = (data['send_quarterly_tips'] == "true");
            },
            error => {
                console.log('error fetching notification settings');
            } 
        );
 
    };
    
    submitChanges(): void {
        this.notificationSettingsForm['send_quarterly_tips'] = (this.sendQuarterlyTips ?  "true" : "false")
        
        this.unsubscribeService.postNotificationSettings( this.notificationSettingsForm )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if (data['redirect_uri'] != null) {
                    window.location.href = data['redirect_uri'];
                } else {
                    console.log("Unexpected error");
                }

            },
            error => {
                console.log('error posting to reset-password-email.json');
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
        this.getNotificationSettingsForm();
    };
}