import * as angular 
    from 'angular';

import { CommonModule } 
    from '@angular/common'; 

import { NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

import { OauthHeaderComponent } from './oauthHeader.component.ts';

// This is the Angular 1 part of the module
export const OauthHeaderModule = angular.module(
    'OauthHeaderModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            OauthHeaderComponent 
        ],
        entryComponents: [ 
            OauthHeaderComponent 
        ],
        imports: [
            CommonModule
        ]
    }
)
export class OauthHeaderNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

OauthHeaderModule.directive(
    'oauthHeaderNg2', 
    <any>downgradeComponent(
        {
            component: OauthHeaderComponent
        }
    )
);