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
import { SelfServiceRemoveContactComponent } 
    from './selfServiceRemoveContact.component.ts';

// This is the Angular 1 part of the module
export const SelfServiceRemoveContactModule = angular.module(
    'SelfServiceRemoveContactModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            SelfServiceRemoveContactComponent
        ],
        entryComponents: [ 
            SelfServiceRemoveContactComponent 
        ],
        imports: [
            CommonModule,
            FormsModule,
        ],
        providers: [
        ]
    }
)
export class SelfServiceRemoveContactNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

SelfServiceRemoveContactModule.directive(
    'selfServiceRemoveContactNg2', 
    <any>downgradeComponent(
        {
            component: SelfServiceRemoveContactComponent
        }
    )
);
