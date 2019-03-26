
import { CommonModule } 
    from '@angular/common'; 

import {  NgModule } 
    from '@angular/core';

import { FormsModule }
    from '@angular/forms'; // <-- NgModel lives here

import {NgbTypeaheadModule} from '@ng-bootstrap/ng-bootstrap'

import { ReCaptchaModule } 
    from 'angular2-recaptcha';

import {BrowserAnimationsModule} 
    from '@angular/platform-browser/animations';

import {MultiSelectModule} 
    from 'primeng/multiselect'

//User generated attribute directives
import { FocusMe }
    from '../../directives/focusMe.directive'; 

//User generated filters

import { AjaxTickDateToISO8601Pipe }
    from '../../pipes/ajaxTickDateToISO8601Ng2'; 

import { AjaxFormDateToISO8601Pipe }
    from '../../pipes/ajaxFormDateToISO8601Ng2'; 

import { ContributorFilterPipe }
    from '../../pipes/contributorFilterNg2';

import { DashToSpacePipe }
    from '../../pipes/dashToSpaceNg2';

import { ExternalIdentifierHtmlPipe }
    from '../../pipes/externalIdentifierHtmlNg2';

import { ExtractContentFromBodyPipe }
    from '../../pipes/extractContentFromBodyNg2';

import { FilterImportWizardsPipe }
    from '../../pipes/filterImportWizardsNg2'

import { KeysPipe }
    from '../../pipes/keysNg2'; 

import { LatexPipe }
    from '../../pipes/latexNg2'; 

import { OrderByAffiliationsPipe }
    from '../../pipes/orderByAffiliationsNg2'; 
    
import { OrderByPipe }
    from '../../pipes/orderByNg2';

import { OrderObjectByPipe }
    from '../../pipes/orderObjectByNg2'

import { ReplaceSeparatorWithSpacePipe }
    from '../../pipes/replaceSeparatorWithSpaceNg2'

import { SafeUrlPipe }
    from '../../pipes/safeUrlNg2';

import { UriPipe }
    from '../../pipes/uriNg2';

import { UrlProtocolPipe }
    from '../../pipes/urlProtocolNg2';

import { WorkExternalIdentifierHtmlPipe }
    from '../../pipes/workExternalIdentifierHtmlNg2';

//User generated modules

import { ExtIdPopoverNg2Module }
    from './../extIdPopover/extIdPopover';

import { LanguageNg2Module }
    from './../language/language';

import { OrgIdentifierPopoverNg2Module } 
    from './../orgIdentifierPopover/orgIdentifierPopover';

import { PrivacytoggleNg2Module }
    from './../privacytoggle/privacyToggle';

import { RequestPasswordResetNg2Module }
    from './../requestPasswordReset/requestPasswordReset';

    
// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            //Angular Libraries
            CommonModule,
            FormsModule,
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
            NgbTypeaheadModule,
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