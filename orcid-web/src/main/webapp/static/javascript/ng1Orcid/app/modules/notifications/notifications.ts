import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { NotificationsComponent } 
    from './notifications.component';

import { NotificationBodyComponent } 
    from './notificationBody.component';

import { CommonNg2Module }
    from './../common/common';

// This is the Angular 1 part of the module
export const NotificationsModule = angular.module(
    'NotificationsModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            NotificationsComponent,
            NotificationBodyComponent
        ],
        entryComponents: [ 
            NotificationsComponent,
            NotificationBodyComponent
        ],
        providers: [
            
        ]
    }
)
export class NotificationsNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
NotificationsModule.directive(
    'notificationsNg2', 
    <any>downgradeComponent(
        {
            component: NotificationsComponent,
        }
    )
    ).directive(
    'notificationBodyNg2',
    <any>downgradeComponent(
        {
            component: NotificationBodyComponent,
        }
    )
);
