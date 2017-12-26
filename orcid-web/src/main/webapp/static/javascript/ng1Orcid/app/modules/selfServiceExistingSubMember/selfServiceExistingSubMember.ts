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
import { SelfServiceExistingSubMemberComponent } 
    from './selfServiceExistingSubMember.component.ts';

// This is the Angular 1 part of the module
export const SelfServiceExistingSubMemberModule = angular.module(
    'SelfServiceExistingSubMemberModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            SelfServiceExistingSubMemberComponent
        ],
        entryComponents: [ 
            SelfServiceExistingSubMemberComponent 
        ],
        imports: [
            CommonModule,
            FormsModule,
        ],
        providers: [
        ]
    }
)
export class SelfServiceExistingSubMemberNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

SelfServiceExistingSubMemberModule.directive(
    'selfServiceExistingSubMemberNg2', 
    <any>downgradeComponent(
        {
            component: SelfServiceExistingSubMemberComponent
        }
    )
);
