import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { PrintRecordComponent } 
    from './printRecord.component';

import { CommonNg2Module }
    from './../common/common';

// This is the Angular 1 part of the module
export const PrintRecordModule = angular.module(
    'PrintRecordModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            PrintRecordComponent
        ],
        entryComponents: [ 
            PrintRecordComponent 
        ],
        providers: [
            
        ]
    }
)
export class PrintRecordNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
PrintRecordModule.directive(
    'printRecordNg2', 
    <any>downgradeComponent(
        {
            component: PrintRecordComponent,
        }
    )
);
