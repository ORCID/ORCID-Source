declare var getBaseUri: any;
declare var scriptTmpl: any;
declare var orcidVar: any;

import * as angular from 'angular';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common'; 
import { NgModule, OnInit } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/switchMap';

import { PrefsSrvc } from './../../services/prefs.service.ts';
import { Preferences } from './../../services/preferences';
import { CommonSrvc } from './../../services/common.service.ts';

//Ng1 hybrid syntax
/*worksPrivacyPreferencesCtrl {


export const worksPrivacyPreferencesCmp = {
    controller: worksPrivacyPreferencesCtrl,
    controllerAs: 'ctrl'
};*/

import { AfterViewInit, Component, Directive, Inject, Injector, Input, ViewChild, ElementRef } from '@angular/core'; 
@Component({
    selector: 'works-privacy-preferences-ng2',
    template:  scriptTmpl("works-privacy-preferences-ng2-template"),
    providers: [PrefsSrvc, CommonSrvc]
})
export class WorksPrivacyPreferencesComponent implements OnInit {
    private response: any;
    default_visibility: string;
    preferences: Preferences[];
    saved: boolean
    
    constructor(

        private prefsSrvc: PrefsSrvc,
        private commonSrvc: CommonSrvc,
        

    ) {

        this.default_visibility = '';
        this.saved = false;
    }

    updateActivitiesVisibilityDefault(priv: string, $event: any): void {
        this.preferences['default_visibility'] = priv;      
        this.prefsSrvc.updateDefaultVisibility(this.preferences).subscribe(
            (response) => {
                this.response = response;
                let preferences_parsed = null;
                preferences_parsed = JSON.parse(JSON.stringify(this.preferences, null, 2));
                this.default_visibility = preferences_parsed.default_visibility;
                this.saved = true;
            },
            (err) => {
                console.log(err);
            },
            () => {}
        );       
    };

    getPreferences(): void {
        this.prefsSrvc.getPreferences().subscribe(
            preferences => {
                let preferences_parsed = null;
                this.preferences = preferences;
                preferences_parsed = JSON.parse(JSON.stringify(this.preferences, null, 2));
                console.log("preferences_parsed", preferences_parsed);
                this.default_visibility = preferences_parsed.default_visibility;
            }
        );
    }

    ngOnInit() {
        this.getPreferences();
        console.log("prefs service init");
    }
}