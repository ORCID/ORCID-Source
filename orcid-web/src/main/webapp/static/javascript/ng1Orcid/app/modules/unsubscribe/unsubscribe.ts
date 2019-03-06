import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { UnsubscribeComponent } 
    from './unsubscribe.component';

import { CommonNg2Module }
    from './../common/common';

// This is the Angular 1 part of the module
export const UnsubscribeModule = angular.module(
    'UnsubscribeModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            UnsubscribeComponent
        ],
        entryComponents: [ 
            UnsubscribeComponent 
        ],
        
    }
)
export class UnsubscribeNg2Module {}

//components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
UnsubscribeModule.directive(
  'unsubscribeNg2', 
  <any>downgradeComponent(
      {
          component: UnsubscribeComponent,
      }
  )
);