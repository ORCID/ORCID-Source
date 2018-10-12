import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { CommonNg2Module }
    from './../common/common.ts';

import { TrustedOrganizationsComponent } 
    from './trustedOrganizations.component.ts';

import { TrustedOrganizationsRevokeComponent } 
    from './trustedOrganizationsRevoke.component.ts';

// This is the Angular 1 part of the module
export const TrustedOrganizationsModule = angular.module(
    'TrustedOrganizationsModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            TrustedOrganizationsComponent,
            TrustedOrganizationsRevokeComponent
        ],
        entryComponents: [ 
            TrustedOrganizationsComponent,
            TrustedOrganizationsRevokeComponent 
        ],
        providers: [
            
        ]
    }
)
export class TrustedOrganizationsNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
TrustedOrganizationsModule.directive(
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

