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

import { AffiliationExtIdPopoverComponent } 
    from './affiliationExtIdPopover.component.ts';

// This is the Angular 1 part of the module
export const AffiliationExtIdPopoverModule = angular.module(
    'AffiliationExtIdPopoverModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            AffiliationExtIdPopoverComponent
        ],
        entryComponents: [ 
            AffiliationExtIdPopoverComponent 
        ],
        exports: [
            AffiliationExtIdPopoverComponent
        ],
        imports: [
            CommonModule,
            FormsModule
        ],
        providers: [
        ]
    }
)
export class AffiliationExtIdPopoverNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

AffiliationExtIdPopoverModule.directive(
    'affiliationExtIdPopoverNg2', 
    <any>downgradeComponent(
        {
            component: AffiliationExtIdPopoverComponent
        }
    )
);
