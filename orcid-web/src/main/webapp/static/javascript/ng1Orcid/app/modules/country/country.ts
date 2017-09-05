import * as angular from 'angular';
import { BrowserModule } from '@angular/platform-browser';
import { Component, Inject, Injector, Input, ViewChild, Directive, ElementRef, NgModule } from '@angular/core';
import { downgradeComponent, UpgradeModule } from '@angular/upgrade/static';

import { CountryComponent } from './country.component.ts';
import { ModalNgComponent } from '../modalNg2/modal-ng.component.ts';

import { CommonService } from '../../shared/commonService.ts';

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
