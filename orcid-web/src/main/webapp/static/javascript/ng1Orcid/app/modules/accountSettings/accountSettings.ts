import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { AccountSettingsComponent } 
    from './accountSettings.component';

import { AltSigninAccountsComponent } 
    from './altSigninAccounts.component';

import { AltSigninAccountsRevokeComponent } 
    from './altSigninAccountsRevoke.component';

import { DelegatesComponent } 
    from './delegates.component';

import { DelegatesAddComponent } 
    from './delegatesAdd.component';

import { DelegatesRevokeComponent } 
    from './delegatesRevoke.component';

import { TrustedOrganizationsComponent } 
    from './trustedOrganizations.component';

import { TrustedOrganizationsRevokeComponent } 
    from './trustedOrganizationsRevoke.component';

import { CommonNg2Module }
    from './../common/common';

import { EmailsFormNg2Module }
    from './../emailsForm/emailsForm';

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
            AccountSettingsComponent,
            AltSigninAccountsComponent,
            AltSigninAccountsRevokeComponent,
            DelegatesComponent,
            DelegatesAddComponent,
            DelegatesRevokeComponent,
            TrustedOrganizationsComponent,
            TrustedOrganizationsRevokeComponent
        ],
        entryComponents: [ 
            AccountSettingsComponent,
            AltSigninAccountsComponent,
            AltSigninAccountsRevokeComponent,
            DelegatesComponent,
            DelegatesAddComponent,
            DelegatesRevokeComponent,
            TrustedOrganizationsComponent,
            TrustedOrganizationsRevokeComponent 
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
    ).directive(
    'altSigninAccountsNg2',
    <any>downgradeComponent(
        {
            component: AltSigninAccountsComponent,
        }
    )
    ).directive(
    'altSigninAccountsRevokeNg2',
    <any>downgradeComponent(
        {
            component: AltSigninAccountsRevokeComponent,
        }
    )
    ).directive(
    'delegatesNg2',
    <any>downgradeComponent(
        {
            component: DelegatesComponent,
        }
    )
    ).directive(
    'delegatesAddNg2',
    <any>downgradeComponent(
        {
            component: DelegatesAddComponent,
        }
    )
    ).directive(
    'delegatesRevokeNg2',
    <any>downgradeComponent(
        {
            component: DelegatesRevokeComponent,
        }
    )
    ).directive(
    'trustedOrganizationsNg2', 
    <any>downgradeComponent(
        {
            component: TrustedOrganizationsComponent,
        }
    )
    ).directive(
    'trustedOrganizationsRevokeNg2',
    <any>downgradeComponent(
        {
            component: TrustedOrganizationsRevokeComponent,
        }
    )
);
