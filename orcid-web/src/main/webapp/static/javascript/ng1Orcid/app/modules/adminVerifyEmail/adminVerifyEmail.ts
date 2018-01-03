import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { AdminVerifyEmailComponent } 
    from './adminVerifyEmail.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const AdminVerifyEmailModule = angular.module(
    'AdminVerifyEmailModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            AdminVerifyEmailComponent
        ],
        entryComponents: [ 
            AdminVerifyEmailComponent 
        ],
        providers: [
            
        ]
    }
)
export class AdminVerifyEmailNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
AdminVerifyEmailModule.directive(
    'adminVerifyEmailNg2', 
    <any>downgradeComponent(
        {
            component: AdminVerifyEmailComponent,
        }
    )
);
