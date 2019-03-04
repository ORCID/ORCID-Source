import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common.ts';

import { ReactivationComponent } 
    from './reactivation.component.ts';

// This is the Angular 1 part of the module
export const ReactivationModule = angular.module(
    'ReactivationModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            ReactivationComponent
        ],
        entryComponents: [ 
            ReactivationComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        
    }
)
export class ReactivationNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

ReactivationModule.directive(
    'reactivationNg2', 
    <any>downgradeComponent(
        {
            component: ReactivationComponent
        }
    )
);
