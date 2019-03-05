import * as angular 
    from 'angular';

import { CommonModule } 
    from '@angular/common'; 

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

import { PrivacytoggleComponent } 
    from './privacyToggle.component';

// This is the Angular 1 part of the module
export const PrivacytoggleModule = angular.module(
    'PrivacytoggleModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonModule
        ],
        declarations: [ 
            PrivacytoggleComponent
        ],
        entryComponents: [ 
            PrivacytoggleComponent 
        ],
        exports: [
            PrivacytoggleComponent
        ]
    }
)
export class PrivacytoggleNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
PrivacytoggleModule.directive(
    'privacytoggleNg2', 
    <any>downgradeComponent(
        {
            component: PrivacytoggleComponent,
        }
    )
);
