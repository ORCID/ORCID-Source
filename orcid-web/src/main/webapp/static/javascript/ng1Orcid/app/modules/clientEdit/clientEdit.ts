import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common.ts';

import { ClientEditComponent } 
    from './clientEdit.component.ts';

// This is the Angular 1 part of the module
export const ClientEditModule = angular.module(
    'ClientEditModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            ClientEditComponent
        ],
        entryComponents: [ 
            ClientEditComponent 
        ],
        imports: [
            CommonNg2Module
        ]
    }
)
export class ClientEditNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

ClientEditModule.directive(
    'clientEditNg2',
    <any>downgradeComponent(
        {
            component: ClientEditComponent
        }
    )
);
