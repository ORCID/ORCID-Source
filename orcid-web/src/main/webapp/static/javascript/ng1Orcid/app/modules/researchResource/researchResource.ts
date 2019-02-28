import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { ResearchResourceComponent } 
    from './researchResource.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const ResearchResourceModule = angular.module(
    'ResearchResourceModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            ResearchResourceComponent
        ],
        entryComponents: [ 
            ResearchResourceComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
            
        ]
    }
)
export class ResearchResourceNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
ResearchResourceModule.directive(
    'researchResourceNg2', 
    <any>downgradeComponent(
        {
            component: ResearchResourceComponent,
        }
    )
);
