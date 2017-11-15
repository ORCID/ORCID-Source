import * as angular 
    from 'angular';

import { CommonModule } 
    from '@angular/common'; 

import { NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//In the end only thanksForVerifyingNg2 should remain
import { ThanksForVerifyingComponent } from './thanksForVerifying.component.ts';

// This is the Angular 1 part of the module
export const ThanksForVerifyingModule = angular.module(
    'ThanksForVerifyingModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            ThanksForVerifyingComponent 
        ],
        entryComponents: [ 
            ThanksForVerifyingComponent 
        ],
        imports: [
            CommonModule
        ]
    }
)
export class ThanksForVerifyingNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

ThanksForVerifyingModule.directive(
    'thanksForVerifyingNg2', 
    <any>downgradeComponent(
        {
            component: ThanksForVerifyingComponent
        }
    )
);
