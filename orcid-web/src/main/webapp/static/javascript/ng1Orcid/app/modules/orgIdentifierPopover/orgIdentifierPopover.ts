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

import { OrgIdentifierPopoverComponent } 
    from './orgIdentifierPopover.component.ts';

// This is the Angular 1 part of the module
export const OrgIdentifierPopoverModule = angular.module(
    'OrgIdentifierPopoverModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            OrgIdentifierPopoverComponent
        ],
        entryComponents: [ 
            OrgIdentifierPopoverComponent 
        ],
        exports: [
            OrgIdentifierPopoverComponent
        ],
        imports: [
            CommonModule,
            FormsModule
        ],
        providers: [
        ]
    }
)
export class OrgIdentifierPopoverNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

OrgIdentifierPopoverModule.directive(
    'orgIdentifierPopoverNg2', 
    <any>downgradeComponent(
        {
            component: OrgIdentifierPopoverComponent
        }
    )
);
