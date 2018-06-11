declare var orcidVar: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';
    
import { CommonService } 
    from '../../shared/common.service.ts';

import { FeaturesService } 
    from '../../shared/features.service.ts'; 

import { PreferencesService } 
    from '../../shared/preferences.service.ts';


@Component({
    selector: 'works-privacy-preferences-ng2',
    template:  scriptTmpl("works-privacy-preferences-ng2-template"),
})
export class WorksPrivacyPreferencesComponent implements OnInit {
    errorUpdatingVisibility: any;
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    prefs: any;
    gdprUiFeatureEnabled: boolean = this.featuresService.isFeatureEnabled('GDPR_UI');
    
    constructor(
        private cdr:ChangeDetectorRef,
        private commonSrvc: CommonService,
        private featuresService: FeaturesService,
        private prefsSrvc: PreferencesService
       
    ) {
        this.errorUpdatingVisibility = false;
        this.prefs = {};
    }

    updateActivitiesVisibilityDefault(oldPriv, newPriv, $event: any): void {
        this.errorUpdatingVisibility = false;
        this.prefsSrvc.updateDefaultVisibility(newPriv)
            .takeUntil(this.ngUnsubscribe)
                .subscribe(
                    response => {
                        this.prefs['default_visibility'] = newPriv;
                        this.cdr.detectChanges();
                    },
                    error => {
                        this.prefs['default_visibility'] = oldPriv; 
                        this.errorUpdatingVisibility = true;
                        this.cdr.detectChanges();
                        // something bad is happening!
                        console.log("error updating preferences");
                    } 
                );    
    };

    getPreferences(): void {
        this.prefsSrvc.getPrivacyPreferences()
            .takeUntil(this.ngUnsubscribe)
                .subscribe(
                    preferences => {
                        this.prefs = preferences;
                        this.cdr.detectChanges();
                    },
                    error => {
                        // something bad is happening!
                        console.log("error getting preferences");
                    } 
                );
    };

    ngOnInit() {
        this.getPreferences();
    }
}