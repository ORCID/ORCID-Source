import * as angular from 'angular';

import { CommonModule } 
    from '@angular/common';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

import { WorksPrivacyPreferencesComponent } 
    from './worksPrivacyPreferences.component.ts';

// This is the Angular 1 part of the module
export const WorksPrivacyPreferencesModule = angular.module(
    'WorksPrivacyPreferencesModule', 
    []
);


// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [ CommonModule ],
        declarations: [ WorksPrivacyPreferencesComponent ],
        entryComponents: [ WorksPrivacyPreferencesComponent ],
        
    }
)
export class WorksPrivacyPreferencesNg2Module {}

WorksPrivacyPreferencesModule.directive(
    'worksPrivacyPreferencesNg2', 
    <any>downgradeComponent(
        {
            component: WorksPrivacyPreferencesComponent,
            //inputs: ['text']
        }
    )
);