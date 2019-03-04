import * as angular 
    from 'angular';

import { CommonModule } 
    from '@angular/common'; 

import {  NgModule } 
    from '@angular/core';

import { FormsModule }
    from '@angular/forms'; // <-- NgModel lives here

import {NgbModule} from '@ng-bootstrap/ng-bootstrap'

import { ReCaptchaModule } 
    from 'angular2-recaptcha';

import {BrowserAnimationsModule} 
    from '@angular/platform-browser/animations';

import {MultiSelectModule} 
    from 'primeng/multiselect'

//User generated attribute directives
import { FocusMe }
    from '../../directives/focusMe.directive.ts'; 

//User generated filters

import { AjaxTickDateToISO8601Pipe }
    from '../../pipes/ajaxTickDateToISO8601Ng2.ts'; 

import { AjaxFormDateToISO8601Pipe }
    from '../../pipes/ajaxFormDateToISO8601Ng2.ts'; 

import { ContributorFilterPipe }
    from '../../pipes/contributorFilterNg2.ts';

import { DashToSpacePipe }
    from '../../pipes/dashToSpaceNg2.ts';

import { ExternalIdentifierHtmlPipe }
    from '../../pipes/externalIdentifierHtmlNg2.ts';

import { ExtractContentFromBodyPipe }
    from '../../pipes/extractContentFromBodyNg2.ts';

import { FilterImportWizardsPipe }
    from '../../pipes/filterImportWizardsNg2.ts'

import { KeysPipe }
    from '../../pipes/keysNg2.ts'; 

import { LatexPipe }
    from '../../pipes/latexNg2.ts'; 

import { OrderByAffiliationsPipe }
    from '../../pipes/orderByAffiliationsNg2.ts'; 
    
import { OrderByPipe }
    from '../../pipes/orderByNg2.ts';

import { OrderObjectByPipe }
    from '../../pipes/orderObjectByNg2.ts'

import { ReplaceSeparatorWithSpacePipe }
    from '../../pipes/replaceSeparatorWithSpaceNg2.ts'

import { SafeUrlPipe }
    from '../../pipes/safeUrlNg2.ts';

import { UriPipe }
    from '../../pipes/uriNg2.ts';

import { UrlProtocolPipe }
    from '../../pipes/urlProtocolNg2.ts';

import { WorkExternalIdentifierHtmlPipe }
    from '../../pipes/workExternalIdentifierHtmlNg2.ts';

//User generated modules

import { ExtIdPopoverNg2Module }
    from './../extIdPopover/extIdPopover.ts';

import { LanguageNg2Module }
    from './../language/language.ts';

import { OrgIdentifierPopoverNg2Module } 
    from './../orgIdentifierPopover/orgIdentifierPopover.ts';

import { PrivacytoggleNg2Module }
    from './../privacytoggle/privacyToggle.ts';

import { RequestPasswordResetNg2Module }
    from './../requestPasswordReset/requestPasswordReset.ts';

    
// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            //Angular Libraries
            CommonModule,
            FormsModule,
            NgbModule,
            BrowserAnimationsModule,
            MultiSelectModule,
            //User Modules
            ExtIdPopoverNg2Module,
            LanguageNg2Module,
            OrgIdentifierPopoverNg2Module,
            PrivacytoggleNg2Module,
            ReCaptchaModule,
            RequestPasswordResetNg2Module,
        ],
        declarations: [ 
            FocusMe,
            AjaxFormDateToISO8601Pipe,
            AjaxTickDateToISO8601Pipe,
            ContributorFilterPipe,
            DashToSpacePipe,
            ExternalIdentifierHtmlPipe,
            ExtractContentFromBodyPipe,
            FilterImportWizardsPipe,
            KeysPipe,
            LatexPipe,
            OrderByAffiliationsPipe,
            OrderByPipe,
            OrderObjectByPipe,
            ReplaceSeparatorWithSpacePipe,
            SafeUrlPipe,
            UriPipe,
            UrlProtocolPipe,
            WorkExternalIdentifierHtmlPipe,

        ],
        exports: [
            //Angular Libraries
            CommonModule,
            FormsModule,
            NgbModule,
            MultiSelectModule,
            //User directives
            FocusMe,
            //User Pipes
            AjaxFormDateToISO8601Pipe,
            AjaxTickDateToISO8601Pipe,
            ContributorFilterPipe,
            DashToSpacePipe,
            ExternalIdentifierHtmlPipe,
            ExtractContentFromBodyPipe,
            FilterImportWizardsPipe,
            KeysPipe,
            LatexPipe,
            OrderByAffiliationsPipe,
            OrderByPipe,
            OrderObjectByPipe,
            ReplaceSeparatorWithSpacePipe,
            SafeUrlPipe,
            UriPipe,
            UrlProtocolPipe,
            WorkExternalIdentifierHtmlPipe,
            //User Modules
            ExtIdPopoverNg2Module,
            LanguageNg2Module,
            OrgIdentifierPopoverNg2Module,
            PrivacytoggleNg2Module,
            ReCaptchaModule,
            RequestPasswordResetNg2Module,
        ]
    }
)
export class CommonNg2Module {}