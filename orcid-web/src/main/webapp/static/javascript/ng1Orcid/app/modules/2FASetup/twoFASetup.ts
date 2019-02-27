import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common';

import { TwoFaSetupComponent } 
    from './twoFASetup.component';

// This is the Angular 1 part of the module
export const TwoFaSetupModule = angular.module(
    'TwoFaSetupModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            TwoFaSetupComponent
        ],
        entryComponents: [ 
            TwoFaSetupComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
        ]
    }
)
export class TwoFaSetupNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

TwoFaSetupModule.directive(
    'twoFaSetupNg2', 
    <any>downgradeComponent(
        {
            component: TwoFaSetupComponent
        }
    )
);
