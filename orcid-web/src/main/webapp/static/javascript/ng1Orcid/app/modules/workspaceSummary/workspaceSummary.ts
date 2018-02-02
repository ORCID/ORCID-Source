import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common.ts';

import { WorkSpaceSummaryComponent } 
    from './workspaceSummary.component.ts';

//User generated filters
import { FilterImportWizardsPipe }
    from '../../pipes/filterImportWizardsNg2.ts'; 

// This is the Angular 1 part of the module
export const WorkSpaceSummaryModule = angular.module(
    'WorkSpaceSummaryModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            WorkSpaceSummaryComponent
        ],
        entryComponents: [ 
            WorkSpaceSummaryComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
        ]
    }
)
export class WorkSpaceSummaryNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

WorkSpaceSummaryModule.directive(
    'workSpaceSummaryNg2', 
    <any>downgradeComponent(
        {
            component: WorkSpaceSummaryComponent
        }
    )
);
