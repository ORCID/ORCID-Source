import * as angular 
    from 'angular';

import { CommonModule } 
    from '@angular/common'; 

import { NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//In the end only emailUnverifiedWarningNg2 should remain
import { EmailUnverifiedWarningComponent } from './emailUnverifiedWarning.component';

// This is the Angular 1 part of the module
export const EmailUnverifiedWarningModule = angular.module(
    'EmailUnverifiedWarningModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            EmailUnverifiedWarningComponent 
        ],
        entryComponents: [ 
            EmailUnverifiedWarningComponent 
        ],
        imports: [
            CommonModule
        ]
    }
)
export class EmailUnverifiedWarningNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

EmailUnverifiedWarningModule.directive(
    'emailUnverifiedWarningNg2', 
    <any>downgradeComponent(
        {
            component: EmailUnverifiedWarningComponent
        }
    )
);
