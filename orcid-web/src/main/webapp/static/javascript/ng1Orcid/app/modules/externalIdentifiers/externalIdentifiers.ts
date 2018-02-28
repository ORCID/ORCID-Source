import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { ExternalIdentifiersComponent } 
    from './externalIdentifiers.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const ExternalIdentifiersModule = angular.module(
    'ExternalIdentifiersModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            ExternalIdentifiersComponent
        ],
        entryComponents: [ 
            ExternalIdentifiersComponent 
        ],
        providers: [
            
        ]
    }
)
export class ExternalIdentifiersNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
ExternalIdentifiersModule.directive(
    'externalIdentifiersNg2', 
    <any>downgradeComponent(
        {
            component: ExternalIdentifiersComponent,
        }
    )
);
