import * as angular 
    from 'angular';

import { CommonModule } 
    from '@angular/common'; 

import { Directive, NgModule } 
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

import { HeaderNg2Module }
    from './../header/header';

import { LanguageNg2Module }
    from './../language/language';

import { OrgIdentifierPopoverNg2Module } 
    from './../orgIdentifierPopover/orgIdentifierPopover';

import { PrivacytoggleNg2Module }
    from './../privacytoggle/privacyToggle';

import { RequestPasswordResetNg2Module }
    from './../requestPasswordReset/requestPasswordReset';

//User generated services

import { AffiliationService } 
    from '../../shared/affiliation.service';

import { AccountService }
    from '../../shared/account.service';

import { AdminActionsService }
    from '../../shared/adminActions.service';
    
import { BlogService }
    from '../../shared/blog.service';

import { ClaimService }
    from '../../shared/claim.service';

import { ClientService }
    from '../../shared/client.service';

import { CommonService }
    from '../../shared/common.service'

import { ConsortiaService }
    from '../../shared/consortia.service'

import { DiscoService }
    from '../../shared/disco.service';

import { EmailService } 
    from '../../shared/email.service';

import { FeaturesService }
    from '../../shared/features.service';

import { FundingService } 
    from '../../shared/funding.service';

import { GenericService } 
    from '../../shared/generic.service';

import { LanguageService }
    from '../../shared/language.service';

import { ManageMembersService } 
    from '../../shared/manageMembers.service';

import { MembersListService }
    from '../../shared/membersList.service'

import { ModalService } 
    from '../../shared/modal.service';

import { NotificationsService } 
    from '../../shared/notifications.service';

import { OauthService }
    from '../../shared/oauth.service';

import { PasswordService }
    from '../../shared/password.service';

import { PeerReviewService }
    from '../../shared/peerReview.service';

import { PreferencesService }
    from '../../shared/preferences.service';

import { ReactivationService }
    from '../../shared/reactivation.service';

import { ResearchResourceService }
    from '../../shared/researchResource.service';

import { SearchService } 
    from '../../shared/search.service';

import { SwitchUserService } 
    from '../../shared/switchUser.service';

import { TwoFAStateService } 
    from '../../shared/twoFAState.service';
    
import { ShibbolethService } 
    from '../../shared/shibboleth.service'; 

import { WidgetService }
    from '../../shared/widget.service'

import { WorkspaceService } 
    from '../../shared/workspace.service'; 

import { WorksService } 
    from '../../shared/works.service';
    
import { EmailFrequencyService }
    from '../../shared/emailFrequency.service';

import { UnsubscribeService }
    from '../../shared/unsubscribe.service';

import { ResendClaimService }
    from '../../shared/resendClaim.service';    

import { DeveloperToolsService }
    from '../../shared/developerTools.service';

import { PersonService } 
    from '../../shared/person.service';
    
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
        ],
        providers: [
            AccountService,
            AdminActionsService,
            AffiliationService,
            BlogService,
            ClaimService,
            ClientService,
            ConsortiaService,
            DiscoService,
            EmailFrequencyService,
            EmailService,
            FeaturesService,
            FundingService,
            GenericService,
            LanguageService,
            ManageMembersService,
            MembersListService,
            ModalService,
            NotificationsService,
            OauthService,
            PasswordService,
            PeerReviewService,
            PreferencesService,
            ReactivationService,
            ResearchResourceService,
            SearchService,
            TwoFAStateService,
            ShibbolethService,
            SwitchUserService,
            WidgetService,
            WorksService,
            WorkspaceService,
            UnsubscribeService,
            ResendClaimService,
            DeveloperToolsService,
            PersonService
        ]
    }
)
export class CommonNg2Module {}