import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { LookUpIdOrEmailComponent } 
    from './lookupIdOrEmail.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const LookUpIdOrEmailModule = angular.module(
    'LookUpIdOrEmailModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            LookUpIdOrEmailComponent
        ],
        entryComponents: [ 
            LookUpIdOrEmailComponent 
        ],
        providers: [
            
        ]
    }
)
export class LookUpIdOrEmailNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
LookUpIdOrEmailModule.directive(
    'lookUpIdOrEmailNg2', 
    <any>downgradeComponent(
        {
            component: LookUpIdOrEmailComponent,
        }
    )
);
