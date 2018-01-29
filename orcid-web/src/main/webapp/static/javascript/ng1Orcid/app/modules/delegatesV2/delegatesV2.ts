import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { DelegatesV2Component } 
    from './delegatesV2.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const DelegatesV2Module = angular.module(
    'DelegatesV2Module', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            DelegatesV2Component
        ],
        entryComponents: [ 
            DelegatesV2Component 
        ],
        providers: [
            
        ]
    }
)
export class DelegatesV2Ng2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
DelegatesV2Module.directive(
    'delegatesV2Ng2', 
    <any>downgradeComponent(
        {
            component: DelegatesV2Component,
        }
    )
);
