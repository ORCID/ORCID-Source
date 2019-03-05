import * as angular 
    from 'angular';

import { CommonModule } 
    from '@angular/common'; 

import { Directive, NgModule } 
    from '@angular/core';

import { FormsModule }
    from '@angular/forms'; // <-- NgModel lives here

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { SelfServiceAddContactComponent } 
    from './selfServiceAddContact.component.ts';

// This is the Angular 1 part of the module
export const SelfServiceAddContactModule = angular.module(
    'SelfServiceAddContactModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            SelfServiceAddContactComponent
        ],
        entryComponents: [ 
            SelfServiceAddContactComponent 
        ],
        imports: [
            CommonModule,
            FormsModule,
        ],
        
    }
)
export class SelfServiceAddContactNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

SelfServiceAddContactModule.directive(
    'selfServiceAddContactNg2', 
    <any>downgradeComponent(
        {
            component: SelfServiceAddContactComponent
        }
    )
);
