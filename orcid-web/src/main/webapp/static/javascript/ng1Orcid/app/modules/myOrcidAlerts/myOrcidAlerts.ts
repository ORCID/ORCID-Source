import * as angular 
    from 'angular';

import { CommonModule } 
    from '@angular/common'; 

import { NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//In the end only myOrcidAlertsNg2 should remain
import { MyOrcidAlertsComponent } from './myOrcidAlerts.component';

// This is the Angular 1 part of the module
export const MyOrcidAlertsModule = angular.module(
    'MyOrcidAlertsModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            MyOrcidAlertsComponent 
        ],
        entryComponents: [ 
            MyOrcidAlertsComponent 
        ],
        imports: [
            CommonModule
        ]
    }
)
export class MyOrcidAlertsNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

MyOrcidAlertsModule.directive(
    'myOrcidAlertsNg2', 
    <any>downgradeComponent(
        {
            component: MyOrcidAlertsComponent
        }
    )
);
