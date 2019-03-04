import * as angular 
    from 'angular';

import { CommonModule } 
    from '@angular/common'; 

import { Directive, NgModule } 
    from '@angular/core';

import { FormsModule }
    from '@angular/forms'; // <-- NgModel lives here

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common';

import { FundingComponent } 
    from './funding.component';

import { PrivacytoggleNg2Module }
    from './../privacytoggle/privacyToggle';

//User generated filters
import { OrderByPipe }
    from '../../pipes/orderByNg2'; 


// This is the Angular 1 part of the module
export const FundingModule = angular.module(
    'FundingModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            FundingComponent,
        ],
        entryComponents: [ 
            FundingComponent 
        ],
        providers: [
            
        ]
    }
)
export class FundingNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
FundingModule.directive(
    'fundingNg2', 
    <any>downgradeComponent(
        {
            component: FundingComponent,
        }
    )
);
