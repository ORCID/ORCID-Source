declare var orcidVar: any;

//Import all the angular components

import { NgFor, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

import { Preferences } 
    from '../../interfaces/preferences.ts';
    
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
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    prefs: any;
    saved: boolean
    gdprUiFeatureEnabled: boolean = this.featuresService.isFeatureEnabled('GDPR_UI');
    
    constructor(
        private cdr:ChangeDetectorRef,
        private commonSrvc: CommonService,
        private featuresService: FeaturesService,
        private prefsSrvc: PreferencesService
       
    ) {
        this.prefs = {};
        this.saved = false;
    }

    updateActivitiesVisibilityDefault(priv: string, $event: any): void {
        this.prefs['default_visibility'] = priv;      
        console.log(this.prefs['default_visibility']);
        this.prefsSrvc.updateDefaultVisibility(this.prefs)
            .takeUntil(this.ngUnsubscribe)
                .subscribe(
                    response => {
                        console.log(response._body);
                        this.cdr.detectChanges();
                    },
                    error => {
                        //TODO show error message on page and reset to previous value
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
        console.log(this.gdprUiFeatureEnabled);
    }
}