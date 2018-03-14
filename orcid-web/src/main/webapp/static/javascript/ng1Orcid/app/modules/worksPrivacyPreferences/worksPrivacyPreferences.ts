import * as angular from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

import { CommonNg2Module }
    from './../common/common.ts';

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
        imports: [ CommonNg2Module ],
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