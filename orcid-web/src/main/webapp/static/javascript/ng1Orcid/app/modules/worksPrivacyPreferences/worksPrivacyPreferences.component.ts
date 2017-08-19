declare var getBaseUri: any;
declare var scriptTmpl: any;
declare var orcidVar: any;

import * as angular from 'angular';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common'; 
import { NgModule } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/switchMap';

import { PrefsSrvc } from './../../services/prefs.service.ts';
import { Preferences } from './../../services/preferences';

//Ng1 hybrid syntax
/*worksPrivacyPreferencesCtrl {


export const worksPrivacyPreferencesCmp = {
    controller: worksPrivacyPreferencesCtrl,
    controllerAs: 'ctrl'
};*/

import { Component, Inject, Input, OnInit } from '@angular/core'; 
@Component({
    selector: 'works-privacy-preferences-ng2',
    template:  scriptTmpl("works-privacy-preferences-ng2-template"),
    providers: [PrefsSrvc]
})
export class WorksPrivacyPreferencesComponent implements OnInit {
    preferences: Preferences[];
    privacyHelp: any;
    showElement: any;
    
    constructor(

        private prefsSrvc: PrefsSrvc

    ) {

        this.privacyHelp = {};
        this.showElement = {};
    }

    /*hideTooltip(el): void {
        this.showElement[el] = false;
    };

    showTooltip(el): void {
        this.showElement[el] = true;
    };

    toggleClickPrivacyHelp(key): void {
        if (document.documentElement.className.indexOf('no-touch')  == -1 ) {
            this.privacyHelp[key]=!this.privacyHelp[key];
        }
    };

    updateActivitiesVisibilityDefault(priv, $event): void {
        this.prefsSrvc.prefs['default_visibility'] = priv;        
        this.prefsSrvc.updateDefaultVisibility();        
    };*/

    getPreferences(): void {
        this.prefsSrvc.getPreferences().subscribe(
            preferences => {
                let preferences_parsed = null;
                this.preferences = preferences;
                preferences_parsed = JSON.parse(JSON.stringify(this.preferences, null, 2));
                console.log("preferences_parsed", preferences_parsed);

                /*this.description = collection_parsed.form.description;
                this.fullOrcidId = collection_parsed.owner.fullOrcidId;
                this.orcid = collection_parsed.owner.orcid;
                this.title = collection_parsed.form.title;
                this.username = collection_parsed.owner.name;

                if ( this.description.length > 0 && this.title.length > 0 ) {
                    this.formEmptyOnLoad = false;
                }*/
            }
        );
    }

    ngOnInit() {
        this.getPreferences();
        console.log("prefs service init");
    }
}