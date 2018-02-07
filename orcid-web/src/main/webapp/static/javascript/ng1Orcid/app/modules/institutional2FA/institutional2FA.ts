import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { Institutional2FAComponent } 
    from './institutional2FA.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const Institutional2FAModule = angular.module(
    'Institutional2FAModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            Institutional2FAComponent
        ],
        entryComponents: [ 
            Institutional2FAComponent 
        ],
        providers: [
            
        ]
    }
)
export class Institutional2FANg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
Institutional2FAModule.directive(
    'institutional2FANg2', 
    <any>downgradeComponent(
        {
            component: Institutional2FAComponent,
        }
    )
);
