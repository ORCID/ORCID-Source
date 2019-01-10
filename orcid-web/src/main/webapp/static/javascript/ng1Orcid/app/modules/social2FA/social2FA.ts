import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { Social2FAComponent } 
    from './social2FA.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';
    
// This is the Angular 1 part of the module
export const Social2FAModule = angular.module(
    'Social2FAModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            Social2FAComponent
        ],
        entryComponents: [ 
            Social2FAComponent 
        ],
        providers: [
        ]
    }
)
export class Social2FANg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
Social2FAModule.directive(
    'social2FANg2', 
    <any>downgradeComponent(
        {
            component: Social2FAComponent,
        }
    )
);
