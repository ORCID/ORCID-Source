import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common.ts';

import { EmailsComponent } 
    from './emails.component.ts';

// This is the Angular 1 part of the module
export const EmailsModule = angular.module(
    'EmailsModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            EmailsComponent
        ],
        entryComponents: [ 
            EmailsComponent 
        ],
        imports: [
            CommonNg2Module
        ]
    }
)
export class EmailsNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

EmailsModule.directive(
    'emailsNg2', 
    <any>downgradeComponent(
        {
            component: EmailsComponent
        }
    )
);
