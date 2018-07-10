import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { DeprecateAccountComponent } 
    from './deprecateAccount.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const DeprecateAccountModule = angular.module(
    'DeprecateAccountModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            DeprecateAccountComponent
        ],
        entryComponents: [ 
            DeprecateAccountComponent 
        ],
        providers: [
            
        ]
    }
)
export class DeprecateAccountNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
DeprecateAccountModule.directive(
    'deprecateAccountNg2', 
    <any>downgradeComponent(
        {
            component:DeprecateAccountComponent,
        }
    )
);