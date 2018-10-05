import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { EditTableComponent } 
    from './editTable.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

import { DeactivateAccountNg2Module }
    from './../deactivateAccount/deactivateAccount.ts';

import { DeprecateAccountNg2Module }
    from './../deprecateAccount/deprecateAccount.ts';

import { EmailsFormNg2Module }
    from './../emailsForm/emailsForm.ts';

import { SecurityQuestionEditNg2Module }
    from './../securityQuestionEdit/securityQuestionEdit.ts';

import { TwoFaStateNg2Module }
    from './../2FAState/twoFAState.ts';

// This is the Angular 1 part of the module
export const EditTableModule = angular.module(
    'EditTableModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module,
            DeactivateAccountNg2Module,
            DeprecateAccountNg2Module,
            EmailsFormNg2Module,
            SecurityQuestionEditNg2Module,
            TwoFaStateNg2Module,
        ],
        declarations: [ 
            EditTableComponent
        ],
        entryComponents: [ 
            EditTableComponent 
        ],
        providers: [
            
        ]
    }
)
export class EditTableNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
EditTableModule.directive(
    'editTableNg2', 
    <any>downgradeComponent(
        {
            component: EditTableComponent,
        }
    )
);
