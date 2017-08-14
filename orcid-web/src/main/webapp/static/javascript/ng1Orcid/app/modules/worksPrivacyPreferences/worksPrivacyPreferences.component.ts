declare var scriptTmpl: any;

import * as angular from 'angular';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';  
import { NgModule } from '@angular/core';
import {UpgradeAdapter} from '@angular/upgrade';
import {commonSrvc} from './../../services/commonSrvc.js';
import {prefsSrvc} from './../../services/prefsSrvc.js';

//Ng1 hybrid syntax
/*worksPrivacyPreferencesCtrl {
    commonSrvc: any;
    prefsSrvc: any;


    static $inject = [
        '$compile',
        '$rootScope',
        '$scope',
        'commonSrvc',
        'prefsSrvc',
    ];

}

export const worksPrivacyPreferencesCmp = {
    controller: worksPrivacyPreferencesCtrl,
    controllerAs: 'ctrl'
};*/


import {Component, Input} from '@angular/core';
@Component({
    selector: 'works-privacy-preferences-ng2',
    template:  scriptTmpl("works-privacy-preferences-ng2-template")
})
export class WorksPrivacyPreferencesComponent {
    privacyHelp: any;
    showElement: any;
    
    constructor(
        commonSrvc: commonSrvc,
        prefsSrvc: prefsSrvc

    ) {
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