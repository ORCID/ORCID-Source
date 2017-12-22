import * as angular 
    from 'angular';

import { CommonModule } 
    from '@angular/common';

import { FormsModule }
    from '@angular/forms';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

import { CommonNg2Module }
    from './../common/common.ts';

//In the end only selfServiceNg2 should remain
import { SelfServiceComponent } from './selfService.component.ts';

// This is the Angular 1 part of the module
export const SelfServiceModule = angular.module(
    'SelfServiceModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            SelfServiceComponent 
        ],
        entryComponents: [ 
            SelfServiceComponent 
        ],
        imports: [
            CommonModule,
            FormsModule,
        ]
    }
)
export class SelfServiceNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

SelfServiceModule.directive(
    'selfServiceNg2', 
    <any>downgradeComponent(
        {
            component: SelfServiceComponent,
            //inputs: ['text']
        }
    )
);
