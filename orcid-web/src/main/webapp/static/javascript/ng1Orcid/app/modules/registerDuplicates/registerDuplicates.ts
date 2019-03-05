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

//User generated components
import { RegisterDuplicatesComponent } 
    from './registerDuplicates.component';

// This is the Angular 1 part of the module
export const RegisterDuplicatesModule = angular.module(
    'RegisterDuplicatesModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            RegisterDuplicatesComponent
        ],
        entryComponents: [ 
            RegisterDuplicatesComponent 
        ],
        imports: [
            CommonModule,
            FormsModule,
        ],
        
    }
)
export class RegisterDuplicatesNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

RegisterDuplicatesModule.directive(
    'registerDuplicatesNg2', 
    <any>downgradeComponent(
        {
            component: RegisterDuplicatesComponent
        }
    )
);