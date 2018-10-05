import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { AccountSettingsComponent } 
    from './accountSettings.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

import { EmailsFormNg2Module }
    from './../emailsForm/emailsForm.ts';

// This is the Angular 1 part of the module
export const AccountSettingsModule = angular.module(
    'AccountSettingsModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module,
            EmailsFormNg2Module,
        ],
        declarations: [ 
            AccountSettingsComponent
        ],
        entryComponents: [ 
            AccountSettingsComponent 
        ],
        providers: [
            
        ]
    }
)
export class AccountSettingsNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
AccountSettingsModule.directive(
    'accountSettingsNg2', 
    <any>downgradeComponent(
        {
            component: AccountSettingsComponent,
        }
    )
);
