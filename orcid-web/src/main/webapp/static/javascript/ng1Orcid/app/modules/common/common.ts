import * as angular 
    from 'angular';

import { CommonModule } 
    from '@angular/common'; 

import { Directive, NgModule } 
    from '@angular/core';

import { FormsModule }
    from '@angular/forms'; // <-- NgModel lives here

//User generated filters
import { OrderByPipe }
    from '../../pipes/orderByNg2.ts';

import { AjaxFormDateToISO8601Pipe }
    from '../../pipes/ajaxFormDateToISO8601Ng2.ts'; 

//User generated modules
import { PrivacytoggleNg2Module }
    from './../privacytoggle/privacyToggle.ts';

import { CountryService } 
    from '../../shared/countryService.ts';

// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            //Angular Libraries
            CommonModule,
            FormsModule,
            //User Modules
            PrivacytoggleNg2Module
        ],
        declarations: [ 
            AjaxFormDateToISO8601Pipe,
            OrderByPipe,
        ],
        exports: [
            //Angular Libraries
            CommonModule,
            FormsModule,
            //User Pipes
            AjaxFormDateToISO8601Pipe,
            OrderByPipe,
            //User Modules
            PrivacytoggleNg2Module
        ],
        providers: [
            CountryService
        ]
    }
)
export class CommonNg2Module {}