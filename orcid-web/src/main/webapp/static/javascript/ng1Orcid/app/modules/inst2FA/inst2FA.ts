import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { InstTwoFactorAuthComponent } 
    from './inst2FA.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const InstTwoFactorAuthModule = angular.module(
    'InstTwoFactorAuthModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            InstTwoFactorAuthComponent
        ],
        entryComponents: [ 
            InstTwoFactorAuthComponent 
        ],
        providers: [
            
        ]
    }
)
export class InstTwoFactorAuthNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
InstTwoFactorAuthModule.directive(
    'insttwofactorauthng2', 
    <any>downgradeComponent(
        {
            component: InstTwoFactorAuthComponent,
        }
    )
);
