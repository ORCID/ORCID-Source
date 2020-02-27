import * as angular 
    from 'angular';

import { CommonModule } 
    from '@angular/common'; 

import { NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//In the end only emailVerificationSentMesssageNg2 should remain
import { SpamSuccessMessageComponent }
    from './spamSuccessMessage.component';

// This is the Angular 1 part of the module
export const SpamSuccessMessageModule = angular.module(
    'SpamSuccessMessageModule',
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [
            SpamSuccessMessageComponent
        ],
        entryComponents: [
            SpamSuccessMessageComponent
        ],
        imports: [
            CommonModule
        ]
    }
)
export class SpamSuccessMessageNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

SpamSuccessMessageModule.directive(
    'spamSuccessMessageNg2',
    <any>downgradeComponent(
        {
            component: SpamSuccessMessageComponent
        }
    )
);
