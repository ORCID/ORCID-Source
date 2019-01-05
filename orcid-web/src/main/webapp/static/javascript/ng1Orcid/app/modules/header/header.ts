import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { HeaderComponent } 
    from './header.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const HeaderModule = angular.module(
    'HeaderModule', 
    []
);


// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            HeaderComponent
        ],
        entryComponents: [ 
            HeaderComponent 
        ],
        providers: [],
    }
)
export class HeaderNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
HeaderModule.directive(
    'headerNg2', 
    <any>downgradeComponent(
        {
            component: HeaderComponent,
        }
    )
);