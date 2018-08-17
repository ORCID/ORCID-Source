import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { WorksMergeChoosePreferredVersionComponent } 
    from './worksMergeChoosePreferredVersion.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const WorksMergeChoosePreferredVersionModule = angular.module(
    'WorksMergeChoosePreferredVersionModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            WorksMergeChoosePreferredVersionComponent
        ],
        entryComponents: [ 
            WorksMergeChoosePreferredVersionComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
            
        ]
    }
)
export class WorksMergeChoosePreferredVersionNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
WorksMergeChoosePreferredVersionModule.directive(
    'worksMergeChoosePreferredVersionNg2', 
    <any>downgradeComponent(
        {
            component: WorksMergeChoosePreferredVersionComponent
        }
    )
);
