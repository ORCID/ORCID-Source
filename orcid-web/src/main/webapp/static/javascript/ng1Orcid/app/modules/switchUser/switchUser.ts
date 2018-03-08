import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { FormsModule }
    from '@angular/forms'; // <-- NgModel lives here

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonModule } 
    from '@angular/common'; 

import { SwitchUserComponent } 
    from './switchUser.component.ts';

//User generated filters
import { OrderByPipe }
    from '../../pipes/orderByNg2Child.ts'; 

// This is the Angular 1 part of the module
export const SwitchUserModule = angular.module(
    'SwitchUserModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            SwitchUserComponent,
            OrderByPipe
        ],
        entryComponents: [ 
            SwitchUserComponent 
        ],
        exports: [
            SwitchUserComponent
        ],
        imports: [
            CommonModule,
            FormsModule
        ],
        providers: [
        ]
    }
)
export class SwitchUserNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

SwitchUserModule.directive(
    'switchUserNg2', 
    <any>downgradeComponent(
        {
            component: SwitchUserComponent
        }
    )
);
