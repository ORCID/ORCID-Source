import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { InternalConsotiumComponent } 
    from './internalConsortium.component';

import { CommonNg2Module }
    from './../common/common';

// This is the Angular 1 part of the module
export const InternalConsotiumModule = angular.module(
    'InternalConsotiumModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            InternalConsotiumComponent
        ],
        entryComponents: [ 
            InternalConsotiumComponent 
        ]
    }
)
export class InternalConsotiumNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
InternalConsotiumModule.directive(
    'internalConsotiumNg2', 
    <any>downgradeComponent(
        {
            component: InternalConsotiumComponent,
        }
    )
);
