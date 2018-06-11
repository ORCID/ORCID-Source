//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { EmailService } 
    from '../../shared/email.service.ts'; 

import { PreferencesService } 
    from '../../shared/preferences.service.ts'; 
    
import { FeaturesService }
    from '../../shared/features.service.ts';    


@Component({
    selector: 'notification-preference-ng2',
    template:  scriptTmpl("notification-preference-ng2-template")
})
export class NotificationPreferenceComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    gdprEmailNotifications: boolean = this.featuresService.isFeatureEnabled('GDPR_EMAIL_NOTIFICATIONS');
    
    constructor(
        private emailSrvc: EmailService,
        private prefsSrvc: PreferencesService,
        private featuresService: FeaturesService        
    ) {
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