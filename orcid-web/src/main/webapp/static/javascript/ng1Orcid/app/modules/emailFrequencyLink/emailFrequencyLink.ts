import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { EmailFrecuencyLinkComponent } 
    from './emailFrequencyLink.component';

import { CommonNg2Module }
    from './../common/common';

// This is the Angular 1 part of the module
export const EmailFrecuencyLinkModule = angular.module(
    'EmailFrecuencyLinkModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            EmailFrecuencyLinkComponent
        ],
        entryComponents: [ 
            EmailFrecuencyLinkComponent 
        ]
    }
)
export class EmailFrecuencyLinkNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
EmailFrecuencyLinkModule.directive(
    'emailFrecuencyLinkNg2', 
    <any>downgradeComponent(
        {
            component: EmailFrecuencyLinkComponent,
        }
    )
);
