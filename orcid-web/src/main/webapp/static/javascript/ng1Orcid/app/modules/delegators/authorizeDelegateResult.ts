import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { AuthorizeDelegateResultComponent } 
    from './authorizeDelegateResult.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';
    
import { APP_BASE_HREF } 
    from '@angular/common'; 
    
import { Routes, RouterModule }  
    from '@angular/router';

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
            CommonNg2Module,
            RouterModule, 
            RouterModule.forRoot([{ path: "reset-password", component: AuthorizeDelegateResultComponent }]),
        ],
        providers: [
            { provide: APP_BASE_HREF, useValue : '/orcid-web/' }
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
