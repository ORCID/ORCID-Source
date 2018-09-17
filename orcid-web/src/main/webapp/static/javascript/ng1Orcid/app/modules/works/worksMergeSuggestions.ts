import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { WorksMergeSuggestionsComponent } 
    from './worksMergeSuggestions.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const WorksMergeSuggestionsModule = angular.module(
    'WorksMergeSuggestionsModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            WorksMergeSuggestionsComponent
        ],
        entryComponents: [ 
            WorksMergeSuggestionsComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
            
        ]
    }
)
export class WorksMergeSuggestionsNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
WorksMergeSuggestionsModule.directive(
    'worksMergeSuggestionsNg2', 
    <any>downgradeComponent(
        {
            component: WorksMergeSuggestionsComponent
        }
    )
);
