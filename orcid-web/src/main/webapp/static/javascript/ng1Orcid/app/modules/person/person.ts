import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common.ts';

import { PersonComponent } 
    from './person.component.ts';

// This is the Angular 1 part of the module
export const PersonModule = angular.module(
    'PersonModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            PersonComponent
        ],
        entryComponents: [ 
            PersonComponent 
        ],
        imports: [
            CommonNg2Module
        ]
    }
)
export class PersonNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

PersonModule.directive(
    'personNg2', 
    <any>downgradeComponent(
        {
            component: PersonComponent
        }
    )
);
