import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { FormsModule }
    from '@angular/forms'; // <-- NgModel lives here

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonModule } 
    from '@angular/common'; 

import { ActivitiesExternalIdentifierComponent } 
    from './activitiesExternalIdentifier.component.ts';

// This is the Angular 1 part of the module
export const ActivitiesExternalIdentifierModule = angular.module(
    'ActivitiesExternalIdentifierModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            ActivitiesExternalIdentifierComponent
        ],
        entryComponents: [ 
            ActivitiesExternalIdentifierComponent 
        ],
        exports: [
            ActivitiesExternalIdentifierComponent
        ],
        imports: [
            CommonModule,
            FormsModule
        ],
        providers: [
        ]
    }
)
export class ActivitiesExternalIdentifierNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

ActivitiesExternalIdentifierModule.directive(
    'activitiesExternalIdentifierNg2', 
    <any>downgradeComponent(
        {
            component: ActivitiesExternalIdentifierComponent
        }
    )
);
