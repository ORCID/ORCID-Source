import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common.ts';

import { AlsoKnownAsFormComponent } 
    from './alsoKnownAsForm.component.ts';

// This is the Angular 1 part of the module
export const AlsoKnownAsFormModule = angular.module(
    'AlsoKnownAsFormModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            AlsoKnownAsFormComponent
        ],
        entryComponents: [ 
            AlsoKnownAsFormComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
        ]
    }
)
export class AlsoKnownAsFormNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

AlsoKnownAsFormModule.directive(
    'alsoKnownAsFormNg2', 
    <any>downgradeComponent(
        {
            component: AlsoKnownAsFormComponent
        }
    )
);
