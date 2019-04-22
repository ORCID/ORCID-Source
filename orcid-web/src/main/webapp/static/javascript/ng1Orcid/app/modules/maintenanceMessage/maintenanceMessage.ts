import * as angular 
    from 'angular';

import { CommonModule } 
    from '@angular/common'; 

import { NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

import { MaintenanceMessageComponent } from './maintenanceMessage.component.ts';

// This is the Angular 1 part of the module
export const MaintenanceMessageModule = angular.module(
    'MaintenanceMessageModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            MaintenanceMessageComponent 
        ],
        entryComponents: [ 
            MaintenanceMessageComponent 
        ],
        imports: [
            CommonModule
        ]
    }
)
export class MaintenanceMessageNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

MaintenanceMessageModule.directive(
    'maintenanceNg2', 
    <any>downgradeComponent(
        {
            component: MaintenanceMessageComponent
        }
    )
);