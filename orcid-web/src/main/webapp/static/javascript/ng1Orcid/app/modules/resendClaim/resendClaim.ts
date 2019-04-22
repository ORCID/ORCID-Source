import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { ResendClaimComponent } 
    from './resendClaim.component';

import { CommonNg2Module }
    from './../common/common';

// This is the Angular 1 part of the module
export const ResendClaimModule = angular.module(
    'ResendClaimModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            ResendClaimComponent
        ],
        entryComponents: [ 
            ResendClaimComponent 
        ],
        
    }
)
export class ResendClaimNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
ResendClaimModule.directive(
    'resendClaimNg2', 
    <any>downgradeComponent(
        {
            component: ResendClaimComponent,
        }
    )
);
