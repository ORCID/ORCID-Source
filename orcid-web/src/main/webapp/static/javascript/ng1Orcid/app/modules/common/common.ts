import * as angular 
    from 'angular';

import { CommonModule } 
    from '@angular/common'; 

import { Directive, NgModule } 
    from '@angular/core';

import { FormsModule }
    from '@angular/forms'; // <-- NgModel lives here

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

import { FilterImportWizardsPipe }
    from '../../pipes/filterImportWizardsNg2.ts'

import { LatexPipe }
    from '../../pipes/latexNg2.ts';  
    
import { OrderByPipe }
    from '../../pipes/orderByNg2.ts';

import { OrderObjectByPipe }
    from '../../pipes/orderObjectByNg2.ts'

import { UrlProtocolPipe }
    from '../../pipes/urlProtocolNg2.ts';

import { WorkExternalIdentifierHtmlPipe }
    from '../../pipes/workExternalIdentifierHtmlNg2.ts';

//User generated modules
import { AffiliationExtIdPopoverNg2Module }
    from './../affiliationExtIdPopover/affiliationExtIdPopover.ts';
/*
import { DelegatesNg2Module }
    from './../delegates/delegates.ts';

import { EditTableNg2Module }
    from './../editTable/editTable.ts';
*/

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

/*
import { RevokeApplicationFormNg2Module }
    from './../revokeApplicationForm/revokeApplicationForm.ts';
*/

import { SecurityQuestionEditNg2Module }
    from './../securityQuestionEdit/securityQuestionEdit.ts';
/*
import { SocialNg2Module }
    from './../social/social.ts';
*/

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

import { PreferencesService }
    from '../../shared/preferences.service.ts';

import { ReactivationService }
    from '../../shared/reactivation.service.ts';

import { SearchService } 
    from '../../shared/search.service.ts';

import { SocialNetworkService }
    from '../../shared/socialNetwork.service.ts';

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


// This is the Angular 2 part of the module
@NgModule(
    {
        imports: [
            //Angular Libraries
            CommonModule,
            FormsModule,
            //User Modules
            AffiliationExtIdPopoverNg2Module,
            //DelegatesNg2Module,
            //EditTableNg2Module,
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
            FilterImportWizardsPipe,
            LatexPipe,
            OrderByPipe,
            OrderObjectByPipe,
            UrlProtocolPipe,
            WorkExternalIdentifierHtmlPipe,

        ],
        exports: [
            //Angular Libraries
            CommonModule,
            FormsModule,
            //User directives
            FocusMe,
            //User Pipes
            AjaxFormDateToISO8601Pipe,
            ContributorFilterPipe,
            FilterImportWizardsPipe,
            LatexPipe,
            OrderByPipe,
            OrderObjectByPipe,
            UrlProtocolPipe,
            WorkExternalIdentifierHtmlPipe,
            //User Modules
            AffiliationExtIdPopoverNg2Module,
            //DelegatesNg2Module,
            //EditTableNg2Module,
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
            PreferencesService,
            ReactivationService,
            SearchService,
            SocialNetworkService,
            TwoFAStateService,
            SwitchUserService,
            WidgetService,
            WorksService,
            WorkspaceService
        ]
    }
)
export class CommonNg2Module {}