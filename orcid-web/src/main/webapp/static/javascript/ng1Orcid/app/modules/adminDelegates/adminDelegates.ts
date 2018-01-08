import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { AdminDelegatesComponent } 
    from './adminDelegates.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const AdminDelegatesModule = angular.module(
    'AdminDelegatesModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            AdminDelegatesComponent
        ],
        entryComponents: [ 
            AdminDelegatesComponent 
        ],
        providers: [
            
        ]
    }
)
export class AdminDelegatesNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
AdminDelegatesModule.directive(
    'adminDelegatesNg2', 
    <any>downgradeComponent(
        {
            component: AdminDelegatesComponent,
        }
    )
);
