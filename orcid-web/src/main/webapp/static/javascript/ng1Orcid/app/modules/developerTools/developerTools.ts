import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common.ts';

import { DeveloperToolsComponent } 
    from './developerTools.component.ts';

// This is the Angular 1 part of the module
export const DeveloperToolsModule = angular.module(
    'DeveloperToolsModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [
            DeveloperToolsComponent
        ],
        entryComponents: [ 
            DeveloperToolsComponent 
        ],
        imports: [
            CommonNg2Module
        ]
    }
)
export class DeveloperToolsNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
DeveloperToolsModule.directive(
    'developerToolsNg2', 
    <any>downgradeComponent(
        {
            component: DeveloperToolsComponent
        }
    )
);
