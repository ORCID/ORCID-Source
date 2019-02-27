import * as angular 
    from 'angular';

import { CommonModule } 
    from '@angular/common';

import { FormsModule }
    from '@angular/forms';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

import { CommonNg2Module }
    from './../common/common';

//In the end only allConsortiumContactsNg2 should remain
import { AllConsortiumContactsComponent } from './allConsortiumContacts.component';

// This is the Angular 1 part of the module
export const AllConsortiumContactsModule = angular.module(
    'AllConsortiumContactsModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            AllConsortiumContactsComponent 
        ],
        entryComponents: [ 
            AllConsortiumContactsComponent 
        ],
        imports: [
            CommonModule,
            FormsModule,
        ]
    }
)
export class AllConsortiumContactsNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

AllConsortiumContactsModule.directive(
    'allConsortiumContactsNg2', 
    <any>downgradeComponent(
        {
            component: AllConsortiumContactsComponent,
            //inputs: ['text']
        }
    )
);
