declare var scriptTmpl: any;

import * as angular from 'angular';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common'; 
import { Component, Inject, Input, ViewChild } from '@angular/core'; 
import { NgModule } from '@angular/core';
import {UpgradeAdapter} from '@angular/upgrade';


//Ng1 hybrid syntax
/*worksPrivacyPreferencesCtrl {


export const worksPrivacyPreferencesCmp = {
    controller: worksPrivacyPreferencesCtrl,
    controllerAs: 'ctrl'
};*/

@Component({
    selector: 'works-privacy-preferences-ng2',
    template:  scriptTmpl("works-privacy-preferences-ng2-template")
})
export class WorksPrivacyPreferencesComponent {
    @ViewChild('modalng2') modalngcomponent;

    prefsSrvc: any;
    privacyHelp: any;
    showElement: any;
    
    constructor(@Inject('prefsSrvc') prefsSrvc) {
    
        this.prefsSrvc = prefsSrvc;
        console.log('this.prefsSrvc', this.prefsSrvc);
        this.privacyHelp = {};
        this.showElement = {};
    }

    hideTooltip(el): void {
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
    };
}