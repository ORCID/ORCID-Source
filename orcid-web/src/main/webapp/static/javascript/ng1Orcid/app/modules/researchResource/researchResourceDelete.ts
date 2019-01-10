import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { ResearchResourceDeleteComponent } 
    from './researchResourceDelete.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const ResearchResourceDeleteModule = angular.module(
    'ResearchResourceDeleteModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            ResearchResourceDeleteComponent
        ],
        entryComponents: [ 
            ResearchResourceDeleteComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
            
        ]
    }
)
export class ResearchResourceDeleteNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
ResearchResourceDeleteModule.directive(
    'researchResourceDeleteNg2', 
    <any>downgradeComponent(
        {
            component: ResearchResourceDeleteComponent,
        }
    )
);
