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
    from './language.component.ts';

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
        ],
        providers: [
        ]
    }
)
export class LanguageNg2Module {}
