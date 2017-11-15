import * as angular 
    from 'angular';

import { CommonModule } 
    from '@angular/common'; 

import { NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//In the end only thanksForRegisteringNg2 should remain
import { ThanksForRegisteringComponent } from './thanksForRegistering.component.ts';

// This is the Angular 1 part of the module
export const ThanksForRegisteringModule = angular.module(
    'ThanksForRegisteringModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            ThanksForRegisteringComponent 
        ],
        entryComponents: [ 
            ThanksForRegisteringComponent 
        ],
        imports: [
            CommonModule
        ]
    }
)
export class ThanksForRegisteringNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

ThanksForRegisteringModule.directive(
    'thanksForRegisteringNg2', 
    <any>downgradeComponent(
        {
            component: ThanksForRegisteringComponent
        }
    )
);
