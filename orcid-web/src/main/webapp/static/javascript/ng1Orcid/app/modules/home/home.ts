import * as angular 
    from 'angular';

import { CommonModule } 
    from '@angular/common'; 

import { NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

import { HomeComponent } from './home.component.ts';

// This is the Angular 1 part of the module
export const HomeModule = angular.module(
    'HomeModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            HomeComponent 
        ],
        entryComponents: [ 
            HomeComponent 
        ],
        imports: [
            CommonModule
        ]
    }
)
export class HomeNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

HomeModule.directive(
    'homeNg2', 
    <any>downgradeComponent(
        {
            component: HomeComponent
        }
    )
);