import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common.ts';

import { WebsitesComponent } 
    from './websites.component.ts';

// This is the Angular 1 part of the module
export const WebsitesModule = angular.module(
    'WebsitesModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            WebsitesComponent
        ],
        entryComponents: [ 
            WebsitesComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
        ]
    }
)
export class WebsitesNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

WebsitesModule.directive(
    'websitesNg2', 
    <any>downgradeComponent(
        {
            component: WebsitesComponent
        }
    )
);
