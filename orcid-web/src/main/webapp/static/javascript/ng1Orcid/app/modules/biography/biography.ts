declare var getBaseUri: any;
declare var logAjaxError: any;

import * as angular from 'angular';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule, NgFor } from '@angular/common'; 
import { Component, Inject, Injector, Input, ViewChild, Directive, ElementRef, NgModule } from '@angular/core';
import { downgradeComponent, UpgradeModule } from '@angular/upgrade/static';
import { FormsModule }   from '@angular/forms'; // <-- NgModel lives here

import { BiographyComponent } from './biography.component.ts';
import { BiographyService } from '../../shared/biographyService.ts'; 

// This is the Angular 1 part of the module
export const BiographyModule = angular.module(
    'BiographyModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonModule,
            FormsModule
        ],
        declarations: [ 
            BiographyComponent
        ],
        entryComponents: [ 
            BiographyComponent 
        ],
        providers: [
            BiographyService
        ]
    }
)
export class BiographyNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
BiographyModule.directive(
    'biographyNg2', 
    <any>downgradeComponent(
        {
            component: BiographyComponent,
        }
    )
);
