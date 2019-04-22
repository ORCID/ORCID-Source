import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common';

import { ClaimComponent } 
    from './claim.component';

// This is the Angular 1 part of the module
export const ClaimModule = angular.module(
    'ClaimModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            ClaimComponent
        ],
        entryComponents: [ 
            ClaimComponent 
        ],
        imports: [
            CommonNg2Module
        ]
    }
)
export class ClaimNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

ClaimModule.directive(
    'claimNg2', 
    <any>downgradeComponent(
        {
            component: ClaimComponent
        }
    )
);
