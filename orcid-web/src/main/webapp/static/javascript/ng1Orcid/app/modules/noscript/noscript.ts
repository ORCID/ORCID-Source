import * as angular 
    from 'angular';

import { CommonModule } 
    from '@angular/common'; 

import { NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

import { NoscriptComponent } from './noscript.component.ts';

// This is the Angular 1 part of the module
export const NoscriptModule = angular.module(
    'NoscriptModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            NoscriptComponent 
        ],
        entryComponents: [ 
            NoscriptComponent 
        ],
        imports: [
            CommonModule
        ]
    }
)
export class NoscriptNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

NoscriptModule.directive(
    'noscriptNg2', 
    <any>downgradeComponent(
        {
            component: NoscriptComponent
        }
    )
);