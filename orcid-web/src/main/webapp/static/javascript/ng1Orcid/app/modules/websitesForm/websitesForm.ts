import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common.ts';

import { WebsitesFormComponent } 
    from './websitesForm.component.ts';

// This is the Angular 1 part of the module
export const WebsitesFormModule = angular.module(
    'WebsitesFormModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            WebsitesFormComponent
        ],
        entryComponents: [ 
            WebsitesFormComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
        ]
    }
)
export class WebsitesFormNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

WebsitesFormModule.directive(
    'websitesFormNg2', 
    <any>downgradeComponent(
        {
            component: WebsitesFormComponent
        }
    )
);
