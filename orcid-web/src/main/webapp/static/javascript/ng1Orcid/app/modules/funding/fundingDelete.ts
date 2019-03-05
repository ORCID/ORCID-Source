import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { FundingDeleteComponent } 
    from './fundingDelete.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const FundingDeleteModule = angular.module(
    'FundingDeleteModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            FundingDeleteComponent
        ],
        entryComponents: [ 
            FundingDeleteComponent 
        ],
        imports: [
            CommonNg2Module
        ]
    }
)
export class FundingDeleteNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
FundingDeleteModule.directive(
    'fundingDeleteNg2', 
    <any>downgradeComponent(
        {
            component: FundingDeleteComponent,
        }
    )
);
