import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

import { AdminActionsComponent } 
    from './adminActions.component';
    
import { ConvertClientConfirmComponent } 
    from './convertClient.component';

import { CommonNg2Module }
    from './../common/common';
import { MoveClientConfirmComponent } from './moveClient.component';
    
// This is the Angular 1 part of the module
export const AdminActionsModule = angular.module(
    'AdminActionsModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            AdminActionsComponent, ConvertClientConfirmComponent, MoveClientConfirmComponent
        ],
        entryComponents: [ 
            AdminActionsComponent, ConvertClientConfirmComponent, MoveClientConfirmComponent
        ]
    }
)
export class AdminActionsNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
AdminActionsModule.directive('adminActionsNg2', <any>downgradeComponent({
  component: AdminActionsComponent,
})).directive('convertClientConfirmNg2', <any>downgradeComponent({
  component: ConvertClientConfirmComponent
})).directive('moveClientConfirmNg2', <any>downgradeComponent({
    component: MoveClientConfirmComponent
  }));
