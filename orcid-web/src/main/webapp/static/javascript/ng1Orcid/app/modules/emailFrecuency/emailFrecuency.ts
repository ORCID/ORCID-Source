import * as angular 
    from 'angular';

import { CommonModule } 
    from '@angular/common'; 

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { EmailFrecuencyComponent } 
    from './emailFrecuency.component.ts';
/*
import { CommonNg2Module }
    from './../common/common.ts';
*/

// This is the Angular 1 part of the module
export const EmailFrecuencyModule = angular.module(
    'EmailFrecuencyModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonModule
        ],
        declarations: [ 
            EmailFrecuencyComponent
        ],
        entryComponents: [ 
            EmailFrecuencyComponent 
        ],
        exports: [ 
            EmailFrecuencyComponent 
        ],
        providers: [
            
        ]
    }
)
export class EmailFrecuencyNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
EmailFrecuencyModule.directive(
    'emailFrecuencyNg2', 
    <any>downgradeComponent(
        {
            component: EmailFrecuencyComponent,
        }
    )
);
