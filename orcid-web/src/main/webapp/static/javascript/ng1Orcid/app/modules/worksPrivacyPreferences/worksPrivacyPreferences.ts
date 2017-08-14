import * as angular from 'angular';
import { NgModule } from '@angular/core';
import { downgradeComponent, UpgradeModule } from '@angular/upgrade/static';

//In the end only widgetNg2 should remain
import { /*widgetCmp, widgetCtrl,*/ WorksPrivacyPreferencesComponent } from './worksPrivacyPreferences.component.ts';

// This is the Angular 1 part of the module
export const WorksPrivacyPreferencesModule = angular.module(
    'WorksPrivacyPreferencesModule', 
    []
);

//WidgetModule.component('widgetCmp', widgetCmp);
//WidgetModule.controller('widgetCtrl', widgetCtrl);

//WorksPrivacyPreferencesModule.component('worksPrivacyPreferencesCmp', worksPrivacyPreferencesCmp);
//WorksPrivacyPreferencesModule.controller('worksPrivacyPreferencesCtrl', worksPrivacyPreferencesCtrl);

// This is the Angular 2 part of the module
@NgModule(
    {
        
        declarations: [ WorksPrivacyPreferencesComponent ],
        entryComponents: [ WorksPrivacyPreferencesComponent ],
        
    }
)
export class WidgetNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

WorksPrivacyPreferencesModule.directive(
    'WorksPrivacyPreferencesNg2', 
    <any>downgradeComponent(
        {
            component: WorksPrivacyPreferencesComponent,
            //inputs: ['text']
        }
    )
);