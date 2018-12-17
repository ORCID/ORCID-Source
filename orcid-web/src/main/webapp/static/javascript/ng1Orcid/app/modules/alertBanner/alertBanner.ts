import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated
import { AlertBannerComponent } 
    from './alertBanner.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            AlertBannerComponent
        ],
        entryComponents: [ 
            AlertBannerComponent 
        ],
        providers: [
            
        ]
    }
)
export class AlertBannerNg2Module {}