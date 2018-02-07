import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { EmailEditComponent } 
    from './emailEdit.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const EmailEditModule = angular.module(
    'EmailEditModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            EmailEditComponent
        ],
        entryComponents: [ 
            EmailEditComponent 
        ],
        providers: [
            
        ]
    }
)
export class EmailEditNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
EmailEditModule.directive(
    'emailEditNg2', 
    <any>downgradeComponent(
        {
            component: EmailEditComponent,
        }
    )
);
