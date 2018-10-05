import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { CommonNg2Module }
    from './../common/common.ts';
    
import { DeactivateAccountComponent } 
    from './deactivateAccount.component.ts';


// This is the Angular 1 part of the module
export const DeactivateAccountModule = angular.module(
    'DeactivateAccountModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            DeactivateAccountComponent
        ],
        entryComponents: [ 
            DeactivateAccountComponent 
        ],
        exports: [ 
            DeactivateAccountComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
            
        ]
    }
)
export class DeactivateAccountNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
DeactivateAccountModule.directive(
    'deactivateAccountNg2', 
    <any>downgradeComponent(
        {
            component: DeactivateAccountComponent,
        }
    )
);
