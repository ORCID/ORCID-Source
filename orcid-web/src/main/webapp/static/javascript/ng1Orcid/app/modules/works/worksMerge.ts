import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { WorksMergeComponent } 
    from './worksMerge.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const WorksMergeModule = angular.module(
    'WorksMergeModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            WorksMergeComponent
        ],
        entryComponents: [ 
            WorksMergeComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
            
        ]
    }
)
export class WorksMergeNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
WorksMergeModule.directive(
    'worksMergeNg2', 
    <any>downgradeComponent(
        {
            component: WorksMergeComponent
        }
    )
);
