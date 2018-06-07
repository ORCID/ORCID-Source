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
    from './../common/common.ts';

import { FundingFormComponent } 
    from './fundingForm.component.ts';

import { PrivacytoggleNg2Module }
    from './../privacytoggle/privacyToggle.ts';

//User generated filters
import { OrderByPipe }
    from '../../pipes/orderByNg2.ts'; 

import { AjaxFormDateToISO8601Pipe }
    from '../../pipes/ajaxFormDateToISO8601Ng2.ts'; 

// This is the Angular 1 part of the module
export const FundingFormModule = angular.module(
    'FundingFormModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            FundingFormComponent,
        ],
        entryComponents: [ 
            FundingFormComponent 
        ],
        providers: [
            
        ]
    }
)
export class FundingFormNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
FundingFormModule.directive(
    'fundingFormNg2', 
    <any>downgradeComponent(
        {
            component: FundingFormComponent,
        }
    )
);
