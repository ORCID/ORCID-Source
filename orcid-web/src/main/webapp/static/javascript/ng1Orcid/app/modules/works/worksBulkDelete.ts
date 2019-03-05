import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { WorksBulkDeleteComponent } 
    from './worksBulkDelete.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const WorksBulkDeleteModule = angular.module(
    'WorksBulkDeleteModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            WorksBulkDeleteComponent
        ],
        entryComponents: [ 
            WorksBulkDeleteComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        
    }
)
export class WorksBulkDeleteNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
WorksBulkDeleteModule.directive(
    'worksBulkDeleteNg2', 
    <any>downgradeComponent(
        {
            component: WorksBulkDeleteComponent,
        }
    )
);
