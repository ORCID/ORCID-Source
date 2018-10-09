import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common.ts';

import { PublicClientComponent } 
    from './publicClient.component.ts';

// This is the Angular 1 part of the module
export const PublicClientModule = angular.module(
    'PublicClientModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            PublicClientComponent
        ],
        entryComponents: [ 
            PublicClientComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
        ]
    }
)
export class PublicClientNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

PublicClientModule.directive(
    'publicClientNg2', 
    <any>downgradeComponent(
        {
            component: PublicClientComponent
        }
    )
);
