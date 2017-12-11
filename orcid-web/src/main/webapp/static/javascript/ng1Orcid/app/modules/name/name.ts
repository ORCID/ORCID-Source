import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { CommonNg2Module }
    from './../common/common.ts';

import { NameComponent } 
    from './name.component.ts';

// This is the Angular 1 part of the module
export const NameModule = angular.module(
    'NameModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            NameComponent
        ],
        entryComponents: [ 
            NameComponent 
        ],
        providers: [
            
        ]
    }
)
export class NameNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
NameModule.directive(
    'nameNg2', 
    <any>downgradeComponent(
        {
            component: NameComponent,
        }
    )
);
