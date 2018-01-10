import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common.ts';

import { TwoFAStateComponent } 
    from './twoFAState.component.ts';

// This is the Angular 1 part of the module
export const TwoFAStateModule = angular.module(
    'TwoFAStateModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            TwoFAStateComponent
        ],
        entryComponents: [ 
            TwoFAStateComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
        ]
    }
)
export class TwoFAStateNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

TwoFAStateModule.directive(
    'twoFAStateNg2', 
    <any>downgradeComponent(
        {
            component: TwoFAStateComponent
        }
    )
);
