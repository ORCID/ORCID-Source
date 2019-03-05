import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { AffiliationComponent } 
    from './affiliation.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const AffiliationModule = angular.module(
    'AffiliationModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            AffiliationComponent
        ],
        entryComponents: [ 
            AffiliationComponent 
        ],
        imports: [
            CommonNg2Module
        ]
    }
)
export class AffiliationNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
AffiliationModule.directive(
    'affiliationNg2', 
    <any>downgradeComponent(
        {
            component: AffiliationComponent,
        }
    )
);
