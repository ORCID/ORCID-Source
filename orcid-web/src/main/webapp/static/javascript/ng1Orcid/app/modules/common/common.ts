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

//User generated attribute directives
import { FocusMe }
    from '../../directives/focusMe.directive.ts'; 

//User generated filters

import { AjaxFormDateToISO8601Pipe }
    from '../../pipes/ajaxFormDateToISO8601Ng2.ts'; 

import { ContributorFilterPipe }
    from '../../pipes/contributorFilterNg2.ts';

import { DashToSpacePipe }
    from '../../pipes/dashToSpaceNg2.ts';

import { ExternalIdentifierHtmlPipe }
    from '../../pipes/externalIdentifierHtmlNg2.ts';

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

import { UrlProtocolPipe }
    from '../../pipes/urlProtocolNg2.ts';

import { WorkExternalIdentifierHtmlPipe }
    from '../../pipes/workExternalIdentifierHtmlNg2.ts';

//User generated modules

import { ExtIdPopoverNg2Module }
    from './../extIdPopover/extIdPopover.ts';

import { HeaderNg2Module }
    from './../header/header.ts';

import { LanguageNg2Module }
    from './../language/language.ts';

import { NotificationPreferenceNg2Module }
    from './../notificationPreferences/notificationPreference.ts';

import { OrgIdentifierPopoverNg2Module } 
    from './../orgIdentifierPopover/orgIdentifierPopover.ts';

import { PasswordEditNg2Module }
    from './../passwordEdit/passwordEdit.ts';

import { PrivacytoggleNg2Module }
    from './../privacytoggle/privacyToggle.ts';

import { RequestPasswordResetNg2Module }
    from './../requestPasswordReset/requestPasswordReset.ts';

import { SecurityQuestionEditNg2Module }
    from './../securityQuestionEdit/securityQuestionEdit.ts';

import { SwitchUserNg2Module }
    from './../switchUser/switchUser.ts';

//User generated services

import { AdminDelegatesService }
    from '../../shared/adminDelegates.service.ts';

import { AffiliationService } 
    from '../../shared/affiliation.service.ts';

import { AccountService }
    from '../../shared/account.service.ts';

import { BlogService }
    from '../../shared/blog.service.ts';

import { ClaimService }
    from '../../shared/claim.service.ts';

import { ClientService }
    from '../../shared/client.service.ts';

import { CommonService }
    from '../../shared/common.service.ts'

import { ConsortiaService }
    from '../../shared/consortia.service.ts'

import { DiscoService }
    from '../../shared/disco.service.ts';

import { EmailService } 
    from '../../shared/email.service.ts';

import { ExternalIdentifiersService } 
    from '../../shared/externalIdentifiers.service.ts';

import { FeaturesService }
    from '../../shared/features.service.ts';

import { FundingService } 
    from '../../shared/funding.service.ts';

import { GenericService } 
    from '../../shared/generic.service.ts';

import { LanguageService }
    from '../../shared/language.service.ts';

import { ManageMembersService } 
    from '../../shared/manageMembers.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts';

import { NotificationsService } 
    from '../../shared/notifications.service.ts';

import { OauthService }
    from '../../shared/oauth.service.ts';

import { PasswordService }
    from '../../shared/password.service.ts';

import { PeerReviewService }
    from '../../shared/peerReview.service.ts';

import { PreferencesService }
    from '../../shared/preferences.service.ts';

import { ReactivationService }
    from '../../shared/reactivation.service.ts';

import { ResearchResourceService }
    from '../../shared/researchResource.service.ts';

import { SearchService } 
    from '../../shared/search.service.ts';

import { SwitchUserService } 
    from '../../shared/switchUser.service.ts';

import { TwoFAStateService } 
    from '../../shared/twoFAState.service.ts';

import { WidgetService }
    from '../../shared/widget.service.ts'

import { WorkspaceService } 
    from '../../shared/workspace.service.ts'; 

import { WorksService } 
    from '../../shared/works.service.ts';
    
import { EmailFrequencyService }
    from '../../shared/emailFrequency.service.ts';

import { UnsubscribeService }
    from '../../shared/unsubscribe.service.ts';

import { ResendClaimService }
    from '../../shared/resendClaim.service.ts';    
    
// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            //Angular Libraries
            CommonModule,
            FormsModule,
            NgbModule,
            //User Modules
            //DelegatesNg2Module,
            //EditTableNg2Module,
            ExtIdPopoverNg2Module,
            LanguageNg2Module,
            OrgIdentifierPopoverNg2Module,
            PrivacytoggleNg2Module,
            ReCaptchaModule,
            RequestPasswordResetNg2Module,
            SwitchUserNg2Module,
        ],
        declarations: [ 
            FocusMe,
            AjaxFormDateToISO8601Pipe,
            ContributorFilterPipe,
            DashToSpacePipe,
            ExternalIdentifierHtmlPipe,
            FilterImportWizardsPipe,
            KeysPipe,
            LatexPipe,
            OrderByAffiliationsPipe,
            OrderByPipe,
            OrderObjectByPipe,
            UrlProtocolPipe,
            WorkExternalIdentifierHtmlPipe,

        ],
        exports: [
            //Angular Libraries
            CommonModule,
            FormsModule,
            NgbModule,
            //User directives
            FocusMe,
            //User Pipes
            AjaxFormDateToISO8601Pipe,
            ContributorFilterPipe,
            DashToSpacePipe,
            ExternalIdentifierHtmlPipe,
            FilterImportWizardsPipe,
            KeysPipe,
            LatexPipe,
            OrderByAffiliationsPipe,
            OrderByPipe,
            OrderObjectByPipe,
            UrlProtocolPipe,
            WorkExternalIdentifierHtmlPipe,
            //User Modules
            //DelegatesNg2Module,
            //EditTableNg2Module,
            ExtIdPopoverNg2Module,
            LanguageNg2Module,
            OrgIdentifierPopoverNg2Module,
            PrivacytoggleNg2Module,
            ReCaptchaModule,
            RequestPasswordResetNg2Module,
            SwitchUserNg2Module,
        ],
        providers: [
            AccountService,
            AdminDelegatesService,
            AffiliationService,
            BlogService,
            ClaimService,
            ClientService,
            CommonService,
            ConsortiaService,
            DiscoService,
            EmailFrequencyService,
            EmailService,
            ExternalIdentifiersService,
            FeaturesService,
            FundingService,
            GenericService,
            LanguageService,
            ManageMembersService,
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
            SwitchUserService,
            WidgetService,
            WorksService,
            WorkspaceService,
            UnsubscribeService,
            ResendClaimService
        ]
    }
)
export class CommonNg2Module {}