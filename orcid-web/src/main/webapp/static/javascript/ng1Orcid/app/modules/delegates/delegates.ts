import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { DelegatesComponent } 
    from './delegates.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const DelegatesModule = angular.module(
    'DelegatesModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            DelegatesComponent
        ],
        entryComponents: [ 
            DelegatesComponent 
        ],
        providers: [
            
        ]
    }
)
export class DelegatesNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
DelegatesModule.directive(
    'delegatesNg2', 
    <any>downgradeComponent(
        {
            component: DelegatesComponent,
        }
    )
);
