import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common.ts';

import { CountryComponent } 
    from './country.component.ts';

// This is the Angular 1 part of the module
export const CountryModule = angular.module(
    'CountryModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            CountryComponent
        ],
        entryComponents: [ 
            CountryComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
        ]
    }
)
export class CountryNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

CountryModule.directive(
    'countryNg2', 
    <any>downgradeComponent(
        {
            component: CountryComponent
        }
    )
);
