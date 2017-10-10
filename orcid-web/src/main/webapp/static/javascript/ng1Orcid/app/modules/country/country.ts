import * as angular 
    from 'angular';

import { CommonModule } 
    from '@angular/common'; 

import { Directive, NgModule } 
    from '@angular/core';

import { FormsModule }
    from '@angular/forms'; // <-- NgModel lives here

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

import { CountryComponent } 
    from './country.component.ts';

import { CommonService } 
    from '../../shared/commonService.ts';

import { CountryService } 
    from '../../shared/countryService.ts';

// This is the Angular 1 part of the module
export const CountryModule = angular.module(
    'CountryModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        imports: [
        ],
        
        declarations: [ 
            CountryComponent
        ],
        entryComponents: [ 
            CountryComponent 
        ],
        providers: [
            CommonService
        ]
    }
)
export class CountryNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

CountryModule.directive(
    'countryNg2test', 
    <any>downgradeComponent(
        {
            component: CountryComponent
        }
    )
);
