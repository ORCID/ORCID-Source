import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common';

import { ClaimThanksComponent } 
    from './claimThanks.component';

// This is the Angular 1 part of the module
export const ClaimThanksModule = angular.module(
    'ClaimThanksModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            ClaimThanksComponent
        ],
        entryComponents: [ 
            ClaimThanksComponent 
        ],
        imports: [
            CommonNg2Module
        ]
    }
)
export class ClaimThanksNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

ClaimThanksModule.directive(
    'claimThanksNg2', 
    <any>downgradeComponent(
        {
            component: ClaimThanksComponent
        }
    )
);
