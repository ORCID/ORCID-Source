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
    from './registerDuplicates.component.ts';

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            RegisterDuplicatesComponent
        ],
        entryComponents: [ 
            RegisterDuplicatesComponent 
        ],
        exports: [
            RegisterDuplicatesComponent 
        ],
        imports: [
            CommonModule,
            FormsModule,
        ],
        providers: [
        ]
    }
)
export class RegisterDuplicatesNg2Module {}