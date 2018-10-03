import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent } 
    from '@angular/upgrade/static';

//User generated
import { RecordCorrectionsComponent } 
    from './recordCorrections.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const RecordCorrectionsModule = angular.module(
    'RecordCorrectionsModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            RecordCorrectionsComponent
        ],
        entryComponents: [ 
            RecordCorrectionsComponent 
        ],
        providers: [
            
        ]
    }
)
export class RecordCorrectionsNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
RecordCorrectionsModule.directive(
    'recordCorrectionsNg2', 
    <any>downgradeComponent(
        {
            component: RecordCorrectionsComponent,
        }
    )
);