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

//User generated services

import { AffiliationService } 
    from '../../shared/affiliation.service.ts';

import { AlsoKnownAsService } 
    from '../../shared/alsoKnownAs.service.ts';

import { BiographyService } 
    from '../../shared/biography.service.ts';

import { CommonService }
    from '../../shared/common.service.ts'

import { CountryService } 
    from '../../shared/country.service.ts';

import { EmailService } 
    from '../../shared/email.service.ts';

import { FundingService } 
    from '../../shared/funding.service.ts';

//import { GroupedActivitiesUtilService } 
//    from '../shared/groupedActivities.service.ts';

import { KeywordsService } 
    from '../../shared/keywords.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts';

import { NameService } 
    from '../../shared/name.service.ts'; 

import { WebsitesService } 
    from '../../shared/websites.service.ts';

import { WorkspaceService } 
    from '../../shared/workspace.service.ts'; 

import { WorksService } 
    from '../../shared/works.service.ts';

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
            AffiliationService,
            AlsoKnownAsService,
            BiographyService,
            CommonService,
            CountryService,
            EmailService,
            FundingService,
            //GroupedActivitiesUtilService,
            KeywordsService,
            ModalService,
            NameService,
            WebsitesService,
            WorksService,
            WorkspaceService
        ]
    }
)
export class CommonNg2Module {}