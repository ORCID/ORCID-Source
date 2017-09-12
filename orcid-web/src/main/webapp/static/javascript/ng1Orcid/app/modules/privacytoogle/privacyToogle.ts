import * as angular 
    from 'angular';

import { CommonModule } 
    from '@angular/common'; 

import { Directive, NgModule } 
    from '@angular/core';

import { FormsModule }
    from '@angular/forms'; // <-- NgModel lives here

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

import { PrivacyToogleComponent } 
    from './privacyToogle.component.ts';

// This is the Angular 1 part of the module
export const PrivacyToogleModule = angular.module(
    'PrivacyToogleModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonModule,
            FormsModule
        ],
        declarations: [ 
            PrivacyToogleComponent
        ],
        entryComponents: [ 
            PrivacyToogleComponent 
        ],
        providers: [
            
        ]
    }
)
export class PrivacyToogleNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
PrivacyToogleModule.directive(
    'privacyToogleNg2', 
    <any>downgradeComponent(
        {
            component: PrivacyToogleComponent,
        }
    )
);
