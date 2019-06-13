import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { Header2Component } 
    from './header2.component';

import { CommonNg2Module }
    from './../common/common';

import {UserMenuComponent} from './user-menu.component'
// This is the Angular 1 part of the module
export const Header2Module = angular.module(
    'Header2Module',
    []
);


// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            Header2Component, UserMenuComponent
        ],
        entryComponents: [ 
            Header2Component 
        ],
        providers: [],
    }
)
export class Header2Ng2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
Header2Module.directive(
    'header2Ng2', 
    <any>downgradeComponent(
        {
            component: Header2Component,
        }
    )
);