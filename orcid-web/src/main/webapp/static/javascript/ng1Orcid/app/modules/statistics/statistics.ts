import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { StatisticsComponent } 
    from './statistics.component';

import { CommonNg2Module }
    from './../common/common';

// This is the Angular 1 part of the module
export const StatisticsModule = angular.module(
    'StatisticsModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            StatisticsComponent
        ],
        entryComponents: [ 
            StatisticsComponent 
        ],
        providers: [
            
        ]
    }
)
export class StatisticsNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
StatisticsModule.directive(
    'statisticsNg2', 
    <any>downgradeComponent(
        {
            component: StatisticsComponent,
        }
    )
);
