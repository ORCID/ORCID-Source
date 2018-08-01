import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common.ts';

import { TwoFaStateComponent } 
    from './twoFAState.component.ts';

// This is the Angular 1 part of the module
export const TwoFaStateModule = angular.module(
    'TwoFaStateModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            TwoFaStateComponent
        ],
        entryComponents: [ 
            TwoFaStateComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
        ]
    }
)
export class TwoFaStateNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

TwoFaStateModule.directive(
    'twoFaStateNg2',
    <any>downgradeComponent(
        {
            component: TwoFaStateComponent
        }
    )
);