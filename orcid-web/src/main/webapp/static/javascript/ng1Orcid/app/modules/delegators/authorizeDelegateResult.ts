import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { AuthorizeDelegateResultComponent } 
    from './authorizeDelegateResult.component';

import { CommonNg2Module }
    from './../common/common';
    
// This is the Angular 1 part of the module
export const AuthorizeDelegateResultModule = angular.module(
    'AuthorizeDelegateResultModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            AuthorizeDelegateResultComponent
        ],
        entryComponents: [ 
            AuthorizeDelegateResultComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
        ]
    }
)
export class AuthorizeDelegateResultNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
AuthorizeDelegateResultModule.directive(
    'authorizeDelegateResultNg2', 
    <any>downgradeComponent(
        {
            component: AuthorizeDelegateResultComponent
        }
    )
);
