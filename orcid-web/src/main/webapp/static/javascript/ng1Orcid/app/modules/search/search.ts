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

import { SearchComponent } 
    from './search.component.ts';

// This is the Angular 1 part of the module
export const SearchModule = angular.module(
    'SearchModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            SearchComponent
        ],
        entryComponents: [ 
            SearchComponent 
        ],
        imports: [
            CommonModule,
            FormsModule
        ],
        
    }
)
export class SearchNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

SearchModule.directive(
    'searchNg2', 
    <any>downgradeComponent(
        {
            component: SearchComponent
        }
    )
);
