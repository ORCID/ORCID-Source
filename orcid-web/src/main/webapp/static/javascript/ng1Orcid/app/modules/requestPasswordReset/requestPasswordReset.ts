import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { FormsModule }
    from '@angular/forms'; // <-- NgModel lives here

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonModule, APP_BASE_HREF } 
    from '@angular/common'; 
    
import { Routes, RouterModule }  
    from '@angular/router';

import { RequestPasswordResetComponent } 
    from './requestPasswordReset.component.ts';

// This is the Angular 1 part of the module
export const RequestPasswordResetModule = angular.module(
    'RequestPasswordResetModule', 
    []
);


// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            RequestPasswordResetComponent
        ],
        entryComponents: [ 
            RequestPasswordResetComponent 
        ],
        exports: [
            RequestPasswordResetComponent
        ],
        imports: [
            CommonModule,
            FormsModule,
            RouterModule, 
            RouterModule.forRoot([{ path: "reset-password", component: RequestPasswordResetComponent }]),
        ],
        providers: [
            { provide: APP_BASE_HREF, useValue : '/orcid-web/' }
        ]
    }
)
export class RequestPasswordResetNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

RequestPasswordResetModule.directive(
    'requestPasswordResetNg2', 
    <any>downgradeComponent(
        {
            component: RequestPasswordResetComponent
        }
    )
);
