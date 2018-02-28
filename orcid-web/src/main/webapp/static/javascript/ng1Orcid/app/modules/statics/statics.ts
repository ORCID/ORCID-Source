import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { StaticsComponent } 
    from './statics.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const StaticsModule = angular.module(
    'StaticsModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            StaticsComponent
        ],
        entryComponents: [ 
            StaticsComponent 
        ],
        providers: [
            
        ]
    }
)
export class StaticsNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
StaticsModule.directive(
    'staticsNg2', 
    <any>downgradeComponent(
        {
            component: StaticsComponent,
        }
    )
);
