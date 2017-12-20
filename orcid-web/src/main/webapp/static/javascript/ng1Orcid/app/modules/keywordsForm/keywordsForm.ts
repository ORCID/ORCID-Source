import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common.ts';

import { KeywordsFormComponent } 
    from './keywordsForm.component.ts';

// This is the Angular 1 part of the module
export const KeywordsFormModule = angular.module(
    'KeywordsFormModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            KeywordsFormComponent
        ],
        entryComponents: [ 
            KeywordsFormComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
        ]
    }
)
export class KeywordsFormNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

KeywordsFormModule.directive(
    'keywordsFormNg2', 
    <any>downgradeComponent(
        {
            component: KeywordsFormComponent
        }
    )
);
