import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

//User generated
import { HeaderComponent } 
    from './header.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            CommonNg2Module
        ],
        declarations: [ 
            HeaderComponent
        ],
        entryComponents: [ 
            HeaderComponent 
        ],
        providers: [],
    }
)
export class HeaderNg2Module {}