import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common.ts';

import { TwoFASetupComponent } 
    from './twoFASetup.component.ts';

// This is the Angular 1 part of the module
export const TwoFASetupModule = angular.module(
    'TwoFASetupModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            TwoFASetupComponent
        ],
        entryComponents: [ 
            TwoFASetupComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
        ]
    }
)
export class TwoFASetupNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

TwoFASetupModule.directive(
    'twoFASetupNg2', 
    <any>downgradeComponent(
        {
            component: TwoFASetupComponent
        }
    )
);
