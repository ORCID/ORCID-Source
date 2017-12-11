import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common.ts';

import { AlsoKnownAsComponent } 
    from './alsoKnownAs.component.ts';

// This is the Angular 1 part of the module
export const AlsoKnownAsModule = angular.module(
    'AlsoKnownAsModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            AlsoKnownAsComponent
        ],
        entryComponents: [ 
            AlsoKnownAsComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
        ]
    }
)
export class AlsoKnownAsNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

AlsoKnownAsModule.directive(
    'alsoKnownAsNg2', 
    <any>downgradeComponent(
        {
            component: AlsoKnownAsComponent
        }
    )
);
