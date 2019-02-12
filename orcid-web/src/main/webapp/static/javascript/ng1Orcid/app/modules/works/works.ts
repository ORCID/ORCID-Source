import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common.ts';

import { WorksComponent } 
    from './works.component.ts';

import { WorksMergeComponent } 
    from './worksMerge.component.ts';

import { WorksMergeWarningComponent } 
    from './worksMergeWarning.component.ts';

//User generated filters
import { FilterImportWizardsPipe }
    from '../../pipes/filterImportWizardsNg2.ts'; 

// This is the Angular 1 part of the module
export const WorksModule = angular.module(
    'WorksModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            WorksComponent,
            WorksMergeComponent,
            WorksMergeWarningComponent 
        ],
        entryComponents: [ 
            WorksComponent,
            WorksMergeComponent,
            WorksMergeWarningComponent  
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
        ]
    }
)
export class WorksNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

WorksModule.directive(
    'worksNg2', 
    <any>downgradeComponent(
        {
            component: WorksComponent
        }
    )
    ).directive(
    'worksMergeNg2',
    <any>downgradeComponent(
        {
            component: WorksMergeComponent,
        }
    )
    ).directive(
    'worksMergeWarningNg2',
    <any>downgradeComponent(
        {
            component: WorksMergeWarningComponent,
        }
    )
);
