import * as angular from 'angular';
import { NgModule } from '@angular/core';
import { downgradeComponent, UpgradeModule } from '@angular/upgrade/static';

//In the end only worksPrivacyPreferencesNg2 should remain
import { /*worksPrivacyPreferencesCmp, worksPrivacyPreferencesCtrl,*/ WorksPrivacyPreferencesComponent } from './worksPrivacyPreferences.component.ts';

// This is the Angular 1 part of the module
export const WorksPrivacyPreferencesModule = angular.module(
    'WorksPrivacyPreferencesModule', 
    []
);


//WorksPrivacyPreferencesModule.component('worksPrivacyPreferencesCmp', worksPrivacyPreferencesCmp);
//WorksPrivacyPreferencesModule.controller('worksPrivacyPreferencesCtrl', worksPrivacyPreferencesCtrl);

// This is the Angular 2 part of the module
@NgModule(
    {
        
        declarations: [ WorksPrivacyPreferencesComponent ],
        entryComponents: [ WorksPrivacyPreferencesComponent ],
        
    }
)
export class WorksPrivacyPreferencesNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

WorksPrivacyPreferencesModule.directive(
    'worksPrivacyPreferencesNg2', 
    <any>downgradeComponent(
        {
            component: WorksPrivacyPreferencesComponent,
            //inputs: ['text']
        }
    )
);