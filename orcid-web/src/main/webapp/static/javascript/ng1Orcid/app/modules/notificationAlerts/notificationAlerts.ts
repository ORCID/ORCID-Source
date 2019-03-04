import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { NotificationAlertsComponent } 
    from './notificationAlerts.component';

import { CommonNg2Module }
    from './../common/common';

// This is the Angular 1 part of the module
export const NotificationAlertsModule = angular.module(
    'NotificationAlertsModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            NotificationAlertsComponent
        ],
        entryComponents: [ 
            NotificationAlertsComponent 
        ],
        providers: [
            
        ]
    }
)
export class NotificationAlertsNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
NotificationAlertsModule.directive(
    'notificationAlertsNg2', 
    <any>downgradeComponent(
        {
            component: NotificationAlertsComponent,
        }
    )
);
