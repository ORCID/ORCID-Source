import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { PersonalInfoComponent } 
    from './personalInfo.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const PersonalInfoModule = angular.module(
    'PersonalInfoModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            PersonalInfoComponent
        ],
        entryComponents: [ 
            PersonalInfoComponent 
        ],
        providers: [
            
        ]
    }
)
export class PersonalInfoNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
PersonalInfoModule.directive(
    'personalInfoNg2', 
    <any>downgradeComponent(
        {
            component: PersonalInfoComponent,
        }
    )
);
