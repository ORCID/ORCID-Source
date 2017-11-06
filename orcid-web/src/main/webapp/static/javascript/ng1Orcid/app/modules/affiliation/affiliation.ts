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

import { AffiliationComponent } 
    from './affiliation.component.ts';

import { PrivacytoggleNg2Module }
    from './../privacytoggle/privacyToggle.ts';

// This is the Angular 1 part of the module
export const AffiliationModule = angular.module(
    'AffiliationModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonModule,
            FormsModule,
            PrivacytoggleNg2Module
        ],
        declarations: [ 
            AffiliationComponent
        ],
        entryComponents: [ 
            AffiliationComponent 
        ],
        providers: [
            
        ]
    }
)
export class AffiliationNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
AffiliationModule.directive(
    'affiliationNg2', 
    <any>downgradeComponent(
        {
            component: AffiliationComponent,
        }
    )
);
