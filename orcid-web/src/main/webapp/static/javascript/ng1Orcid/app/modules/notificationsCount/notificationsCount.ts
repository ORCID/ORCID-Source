import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { NotificationsCountComponent } 
    from './notificationsCount.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const NotificationsCountModule = angular.module(
    'NotificationsCountModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            NotificationsCountComponent
        ],
        entryComponents: [ 
            NotificationsCountComponent 
        ],
        providers: [
            
        ]
    }
)
export class NotificationsCountNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
NotificationsCountModule.directive(
    'notificationsCountNg2', 
    <any>downgradeComponent(
        {
            component: NotificationsCountComponent,
        }
    )
);
