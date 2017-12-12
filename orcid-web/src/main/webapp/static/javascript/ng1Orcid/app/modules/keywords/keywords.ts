import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common.ts';

import { KeywordsComponent } 
    from './keywords.component.ts';

// This is the Angular 1 part of the module
export const KeywordsModule = angular.module(
    'KeywordsModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            KeywordsComponent
        ],
        entryComponents: [ 
            KeywordsComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
        ]
    }
)
export class KeywordsNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

KeywordsModule.directive(
    'keywordsNg2', 
    <any>downgradeComponent(
        {
            component: KeywordsComponent
        }
    )
);
