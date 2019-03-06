import * as angular 
    from 'angular';

import { CommonModule } 
    from '@angular/common'; 

import { NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

import { HtmlHeadComponent } from './htmlHead.component.ts';

// This is the Angular 1 part of the module
export const HtmlHeadModule = angular.module(
    'HtmlHeadModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            HtmlHeadComponent 
        ],
        entryComponents: [ 
            HtmlHeadComponent 
        ],
        imports: [
            CommonModule
        ]
    }
)
export class HtmlHeadNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

HtmlHeadModule.directive(
    'htmlHeadNg2', 
    <any>downgradeComponent(
        {
            component: HtmlHeadComponent
        }
    )
);