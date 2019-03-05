import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { WorksDeleteComponent } 
    from './worksDelete.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const WorksDeleteModule = angular.module(
    'WorksDeleteModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            WorksDeleteComponent
        ],
        entryComponents: [ 
            WorksDeleteComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        
    }
)
export class WorksDeleteNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
WorksDeleteModule.directive(
    'worksDeleteNg2', 
    <any>downgradeComponent(
        {
            component: WorksDeleteComponent,
        }
    )
);
