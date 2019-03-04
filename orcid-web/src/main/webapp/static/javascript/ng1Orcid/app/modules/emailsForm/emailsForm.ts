import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common';

import { EmailsFormComponent } 
    from './emailsForm.component';

// This is the Angular 1 part of the module
export const EmailsFormModule = angular.module(
    'EmailsFormModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            EmailsFormComponent
        ],
        entryComponents: [ 
            EmailsFormComponent 
        ],
        exports: [ 
            EmailsFormComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
        ],
    }
)
export class EmailsFormNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

EmailsFormModule.directive(
    'emailsFormNg2', 
    <any>downgradeComponent(
        {
            component: EmailsFormComponent
        }
    )
);
