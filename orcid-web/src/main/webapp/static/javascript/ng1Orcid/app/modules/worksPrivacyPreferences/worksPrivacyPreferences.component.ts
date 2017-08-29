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
    preferences: Preferences[];
    privacyHelp: any;
    showElement: any;
    developer_tools_enabled: boolean
    send_member_update_requests: boolean
    send_orcid_news: boolean
    send_change_notifications: boolean
    email_frequency: string;
    default_visibility: string;
    notifications_enabled: boolean
    send_administrative_change_notifications: boolean
    
    constructor(

        private prefsSrvc: PrefsSrvc,
        private commonSrvc: CommonSrvc,
        

    ) {

        this.privacyHelp = {};
        this.showElement = {};
        this.developer_tools_enabled = false;
        this.send_member_update_requests = false;
        this.send_orcid_news = false;
        this.send_change_notifications = false;
        this.email_frequency = '';
        this.default_visibility = '';
        this.notifications_enabled = false;
        this.send_administrative_change_notifications = false;
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
    };*/

    updateActivitiesVisibilityDefault(priv: string, $event: any): void {
        this.preferences['default_visibility'] = priv;      
        this.prefsSrvc.updateDefaultVisibility(this.preferences).subscribe(
            (response) => {
                this.response = response;
                console.log(this.preferences);
                let preferences_parsed = null;
                preferences_parsed = JSON.parse(JSON.stringify(this.preferences, null, 2));
                console.log("preferences_parsed", preferences_parsed);
                this.default_visibility = preferences_parsed.default_visibility;
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