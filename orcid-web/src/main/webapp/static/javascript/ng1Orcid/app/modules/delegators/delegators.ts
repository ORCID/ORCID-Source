import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { DelegatorsComponent } 
    from './delegators.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const DelegatorsModule = angular.module(
    'DelegatorsModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            DelegatorsComponent
        ],
        entryComponents: [ 
            DelegatorsComponent 
        ],
        providers: [
            
        ]
    }
)
export class DelegatorsNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
DelegatorsModule.directive(
    'delegatorsNg2', 
    <any>downgradeComponent(
        {
            component: DelegatorsComponent,
        }
    )
);
