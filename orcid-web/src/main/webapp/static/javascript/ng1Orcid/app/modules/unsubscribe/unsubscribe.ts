import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { UnsubscribeComponent } 
    from './unsubscribe.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const UnsubscribeModule = angular.module(
    'UnsubscribeModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            UnsubscribeComponent
        ],
        entryComponents: [ 
            UnsubscribeComponent 
        ],
        providers: [
            
        ]
    }
)
export class UnsubscribeNg2Module {}