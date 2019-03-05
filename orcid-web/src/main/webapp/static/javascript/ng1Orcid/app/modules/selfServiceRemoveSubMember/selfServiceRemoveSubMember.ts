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
import { SelfServiceRemoveSubMemberComponent } 
    from './selfServiceRemoveSubMember.component';

// This is the Angular 1 part of the module
export const SelfServiceRemoveSubMemberModule = angular.module(
    'SelfServiceRemoveSubMemberModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            SelfServiceRemoveSubMemberComponent
        ],
        entryComponents: [ 
            SelfServiceRemoveSubMemberComponent 
        ],
        imports: [
            CommonModule,
            FormsModule,
        ],
        
    }
)
export class SelfServiceRemoveSubMemberNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

SelfServiceRemoveSubMemberModule.directive(
    'selfServiceRemoveSubMemberNg2', 
    <any>downgradeComponent(
        {
            component: SelfServiceRemoveSubMemberComponent
        }
    )
);
