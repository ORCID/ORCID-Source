import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common.ts';

import { CustomEmailComponent } 
    from './customEmail.component.ts';

// This is the Angular 1 part of the module
export const CustomEmailModule = angular.module(
    'CustomEmailModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            CustomEmailComponent
        ],
        entryComponents: [ 
            CustomEmailComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
        ]
    }
)
export class CustomEmailNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

CustomEmailModule.directive(
    'CustomEmailNg2', 
    <any>downgradeComponent(
        {
            component: CustomEmailComponent
        }
    )
);
