import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { NotificationPreferenceComponent } 
    from './notificationPreference.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const NotificationPreferenceModule = angular.module(
    'NotificationPreferenceModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            NotificationPreferenceComponent
        ],
        entryComponents: [ 
            NotificationPreferenceComponent 
        ],
        providers: [
            
        ]
    }
)
export class NotificationPreferenceNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
NotificationPreferenceModule.directive(
    'notificationPreferenceNg2', 
    <any>downgradeComponent(
        {
            component: NotificationPreferenceComponent,
        }
    )
);
