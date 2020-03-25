import * as angular 
    from 'angular';

import { CommonModule } 
    from '@angular/common'; 

import { NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

import { SpamErrorMessageComponent }
    from './spamErrorMessage.component';

// This is the Angular 1 part of the module
export const SpamErrorMessageModule = angular.module(
    'SpamErrorMessageModule',
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [
            SpamErrorMessageComponent
        ],
        entryComponents: [
            SpamErrorMessageComponent
        ],
        imports: [
            CommonModule
        ]
    }
)
export class SpamErrorMessageNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

SpamErrorMessageModule.directive(
    'spamErrorMessageNg2',
    <any>downgradeComponent(
        {
            component: SpamErrorMessageComponent
        }
    )
);
