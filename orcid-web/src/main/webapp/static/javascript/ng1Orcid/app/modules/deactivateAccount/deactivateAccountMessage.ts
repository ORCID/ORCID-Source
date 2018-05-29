import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { CommonNg2Module }
    from './../common/common.ts';
    
import { DeactivateAccountMessageComponent } 
    from './deactivateAccountMessage.component.ts';


// This is the Angular 1 part of the module
export const DeactivateAccountMessageModule = angular.module(
    'DeactivateAccountMessageModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            DeactivateAccountMessageComponent
        ],
        entryComponents: [ 
            DeactivateAccountMessageComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
            
        ]
    }
)
export class DeactivateAccountMessageNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
DeactivateAccountMessageModule.directive(
    'deactivateAccountMessageNg2', 
    <any>downgradeComponent(
        {
            component: DeactivateAccountMessageComponent,
        }
    )
);
