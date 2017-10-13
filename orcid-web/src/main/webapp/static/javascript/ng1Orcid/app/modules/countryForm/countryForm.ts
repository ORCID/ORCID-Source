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

import { CountryFormComponent } 
    from './countryForm.component.ts';

import { PrivacytoggleComponent } 
    from './../privacytoggle/privacyToggle.component.ts';

// This is the Angular 1 part of the module
export const CountryFormModule = angular.module(
    'CountryFormModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            CountryFormComponent,
            //PrivacytoggleComponent
        ],
        entryComponents: [ 
            CountryFormComponent 
        ],
        imports: [
            CommonModule,
            FormsModule
        ],
        providers: [
        ]
    }
)
export class CountryFormNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

CountryFormModule.directive(
    'countryFormNg2', 
    <any>downgradeComponent(
        {
            component: CountryFormComponent
        }
    )
);
