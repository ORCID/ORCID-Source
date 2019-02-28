import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { FormsModule }
    from '@angular/forms'; // <-- NgModel lives here

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonModule } 
    from '@angular/common'; 

import { ExtIdPopoverComponent } 
    from './extIdPopover.component.ts';

// This is the Angular 1 part of the module
export const ExtIdPopoverModule = angular.module(
    'ExtIdPopoverModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            ExtIdPopoverComponent
        ],
        entryComponents: [ 
            ExtIdPopoverComponent 
        ],
        exports: [
            ExtIdPopoverComponent
        ],
        imports: [
            CommonModule,
            FormsModule
        ],
        providers: [
        ]
    }
)
export class ExtIdPopoverNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

ExtIdPopoverModule.directive(
    'extIdPopoverNg2', 
    <any>downgradeComponent(
        {
            component: ExtIdPopoverComponent
        }
    )
);
