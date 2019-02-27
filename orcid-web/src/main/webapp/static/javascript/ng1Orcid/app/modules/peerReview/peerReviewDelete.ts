import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { PeerReviewDeleteComponent } 
    from './peerReviewDelete.component';

import { CommonNg2Module }
    from './../common/common';

// This is the Angular 1 part of the module
export const PeerReviewDeleteModule = angular.module(
    'PeerReviewDeleteModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            PeerReviewDeleteComponent
        ],
        entryComponents: [ 
            PeerReviewDeleteComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
            
        ]
    }
)
export class PeerReviewDeleteNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
PeerReviewDeleteModule.directive(
    'peerReviewDeleteNg2', 
    <any>downgradeComponent(
        {
            component: PeerReviewDeleteComponent,
        }
    )
);
