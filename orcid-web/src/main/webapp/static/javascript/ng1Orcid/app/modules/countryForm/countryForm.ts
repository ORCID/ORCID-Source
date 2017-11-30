import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common.ts';

import { CountryFormComponent } 
    from './countryForm.component.ts';

// This is the Angular 1 part of the module
export const CountryFormModule = angular.module(
    'CountryFormModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            CountryFormComponent
        ],
        entryComponents: [ 
            CountryFormComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
        ]
    }
)
export class CountryFormNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

CountryFormModule.directive(
    'countryFormNg2', 
    <any>downgradeComponent(
        {
            component: CountryFormComponent
        }
    )
);
