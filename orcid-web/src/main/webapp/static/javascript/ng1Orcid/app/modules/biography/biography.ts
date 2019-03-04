import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { BiographyComponent } 
    from './biography.component';

import { CommonNg2Module }
    from './../common/common';

// This is the Angular 1 part of the module
export const BiographyModule = angular.module(
    'BiographyModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            BiographyComponent
        ],
        entryComponents: [ 
            BiographyComponent 
        ],
        providers: [
            
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
