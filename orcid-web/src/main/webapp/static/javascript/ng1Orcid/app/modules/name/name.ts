import * as angular 
    from 'angular';

import { CommonModule } 
    from '@angular/common'; 

import { Directive, NgModule } 
    from '@angular/core';

import { FormsModule }
    from '@angular/forms'; // <-- NgModel lives here

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

import { NameComponent } 
    from './name.component.ts';

import { PrivacytoggleComponent } 
    from './../privacytoggle/privacyToggle.component.ts';

import { PrivacytoggleNg2Module }
    from './../privacytoggle/privacyToggle.ts';

// This is the Angular 1 part of the module
export const NameModule = angular.module(
    'NameModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonModule,
            FormsModule,
            //PrivacytoggleNg2Module
        ],
        declarations: [ 
            NameComponent,
            //PrivacytoggleComponent
        ],
        entryComponents: [ 
            NameComponent 
        ],
        providers: [
            
        ]
    }
)
export class NameNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
NameModule.directive(
    'nameNg2', 
    <any>downgradeComponent(
        {
            component: NameComponent,
        }
    )
);
