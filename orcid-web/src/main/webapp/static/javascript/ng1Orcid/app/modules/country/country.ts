declare var getBaseUri: any;
declare var logAjaxError: any;

import * as angular from 'angular';
import { NgModule } from '@angular/core';
import { downgradeComponent, UpgradeModule } from '@angular/upgrade/static';

//In the end only countryNg2 should remain
import { /*countryCmp, countryCtrl,*/ CountryComponent } from './country.component.ts';

// This is the Angular 1 part of the module
export const CountryModule = angular.module(
    'CountryModule', 
    []
);

//WidgetModule.component('countryCmp', countryCmp);
//WidgetModule.controller('countryCtrl', countryCtrl);

// This is the Angular 2 part of the module
@NgModule(
    {
        
        declarations: [ CountryModule ],
        entryComponents: [ CountryModule ]
    }
)
export class CountryNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

CountryModule.directive(
    'countryNg2', 
    <any>downgradeComponent(
        {
            component: CountryModule
        }
    )
);
