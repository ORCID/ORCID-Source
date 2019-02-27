import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { AffiliationDeleteComponent } 
    from './affiliationDelete.component';

import { CommonNg2Module }
    from './../common/common';

// This is the Angular 1 part of the module
export const AffiliationDeleteModule = angular.module(
    'AffiliationDeleteModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            AffiliationDeleteComponent
        ],
        entryComponents: [ 
            AffiliationDeleteComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
            
        ]
    }
)
export class AffiliationDeleteNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
AffiliationDeleteModule.directive(
    'affiliationDeleteNg2', 
    <any>downgradeComponent(
        {
            component: AffiliationDeleteComponent,
        }
    )
);
