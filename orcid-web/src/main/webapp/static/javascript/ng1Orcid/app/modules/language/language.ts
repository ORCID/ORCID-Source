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

import { LanguageComponent } 
    from './language.component';

// This is the Angular 1 part of the module
export const LanguageModule = angular.module(
    'LanguageModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            LanguageComponent
        ],
        entryComponents: [ 
            LanguageComponent 
        ],
        exports: [
            LanguageComponent
        ],
        imports: [
            CommonModule,
            FormsModule
        ]
    }
)
export class LanguageNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

LanguageModule.directive(
    'languageNg2', 
    <any>downgradeComponent(
        {
            component: LanguageComponent
        }
    )
);
