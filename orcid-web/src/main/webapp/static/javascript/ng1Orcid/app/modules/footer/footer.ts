import * as angular 
    from 'angular';

import { CommonModule } 
    from '@angular/common'; 

import { NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

import { FooterComponent } from './footer.component.ts';

// This is the Angular 1 part of the module
export const FooterModule = angular.module(
    'FooterModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            FooterComponent 
        ],
        entryComponents: [ 
            FooterComponent 
        ],
        imports: [
            CommonModule
        ]
    }
)
export class FooterNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

FooterModule.directive(
    'footerNg2', 
    <any>downgradeComponent(
        {
            component: FooterComponent
        }
    )
);