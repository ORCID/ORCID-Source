import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { SecurityQuestionEditComponent } 
    from './securityQuestionEdit.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const SecurityQuestionEditModule = angular.module(
    'SecurityQuestionEditModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            SecurityQuestionEditComponent
        ],
        entryComponents: [ 
            SecurityQuestionEditComponent 
        ],
        exports: [ 
            SecurityQuestionEditComponent
        ],
        providers: [
            
        ]
    }
)
export class SecurityQuestionEditNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
SecurityQuestionEditModule.directive(
    'securityQuestionEditNg2', 
    <any>downgradeComponent(
        {
            component: SecurityQuestionEditComponent,
        }
    )
);
