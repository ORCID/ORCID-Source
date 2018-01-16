import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { PublicEduAffiliationComponent } 
    from './publicEduAffiliation.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const PublicEduAffiliationModule = angular.module(
    'PublicEduAffiliationModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            PublicEduAffiliationComponent
        ],
        entryComponents: [ 
            PublicEduAffiliationComponent 
        ],
        providers: [
            
        ]
    }
)
export class PublicEduAffiliationNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
PublicEduAffiliationModule.directive(
    'publicEduAffiliationNg2', 
    <any>downgradeComponent(
        {
            component: PublicEduAffiliationComponent,
        }
    )
);
