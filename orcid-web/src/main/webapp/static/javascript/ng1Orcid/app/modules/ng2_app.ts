//Angular imports
import 'reflect-metadata';

import { CommonModule } 
    from '@angular/common'; 

import { HttpClientModule } 
    from '@angular/common/http';

import { Component, NgModule } 
    from '@angular/core';

import { FormsModule } 
    from '@angular/forms';

import { HttpModule, JsonpModule, Request, XSRFStrategy } 
    from '@angular/http';

import { BrowserModule } 
    from "@angular/platform-browser";

import { platformBrowserDynamic } 
    from '@angular/platform-browser-dynamic';

import { RouterModule, UrlHandlingStrategy } 
    from '@angular/router';

import { UpgradeModule } 
    from '@angular/upgrade/static';

import {NgbModule} from '@ng-bootstrap/ng-bootstrap'

//User generated modules imports

import { AffiliationNg2Module } 
    from './affiliation/affiliation.ts';

import { AffiliationDeleteNg2Module } 
    from './affiliation/affiliationDelete.ts';

import { AffiliationFormNg2Module } 
    from './affiliation/affiliationForm.ts';

import { AlertBannerNg2Module } 
    from './alertBanner/alertBanner.ts';

import { AllConsortiumContactsNg2Module } 
    from './allConsortiumContacts/allConsortiumContacts.ts';

import { BiographyNg2Module } 
    from './biography/biography.ts';

import { ClaimThanksNg2Module } 
    from './claimThanks/claimThanks.ts';

import { ClientEditNg2Module } 
    from './clientEdit/clientEdit.ts';

import { DeactivateAccountNg2Module }
    from './deactivateAccount/deactivateAccount.ts';

import { DeactivateAccountMessageNg2Module }
    from './deactivateAccount/deactivateAccountMessage.ts';

import { DeprecateAccountNg2Module }
    from './deprecateAccount/deprecateAccount.ts';

import { EmailsNg2Module } 
    from './emails/emails.ts';

import { EmailsFormNg2Module } 
    from './emailsForm/emailsForm.ts';

import { EmailUnverifiedWarningNg2Module } 
    from './emailUnverifiedWarning/emailUnverifiedWarning.ts';

import { EmailVerificationSentMesssageNg2Module } 
    from './emailVerificationSentMessage/emailVerificationSentMessage.ts';

import { ExternalIdentifiersNg2Module }
    from './externalIdentifiers/externalIdentifiers.ts';

import { ExtIdPopoverNg2Module } 
    from './extIdPopover/extIdPopover.ts';

import { FundingDeleteNg2Module } 
    from './funding/fundingDelete.ts';

import { FundingNg2Module } 
    from './funding/funding.ts';

import { FundingFormNg2Module } 
    from './funding/fundingForm.ts';

import { HomeNg2Module } 
    from './home/home.ts';

import { HeaderNg2Module } 
    from './header/header.ts';

import { LanguageNg2Module }
    from './language/language.ts';

import { LinkAccountNg2Module } 
    from './linkAccount/linkAccount.ts';

import { ModalNg2Module }
    from './modalNg2/modal-ng.ts';

import { NameNg2Module } 
    from './name/name.ts';

import { NotificationsNg2Module }
    from './notifications/notifications.ts';

import { PasswordEditNg2Module } 
    from './passwordEdit/passwordEdit.ts';

import { PersonNg2Module } 
    from './person/person.ts';

import { PersonalInfoNg2Module } 
    from './personalInfo/personalInfo.ts';

import { PublicEduAffiliationNg2Module }
    from './publicEduAffiliation/publicEduAffiliation.ts';

import { OauthAuthorizationNg2Module } 
    from './oauthAuthorization/oauthAuthorization.ts';

import { OrgIdentifierPopoverNg2Module } 
    from './orgIdentifierPopover/orgIdentifierPopover.ts';

import { ReactivationNg2Module } 
    from './reactivation/reactivation.ts';

import { RegisterDuplicatesNg2Module } 
    from './registerDuplicates/registerDuplicates.ts';

import { RequestPasswordResetNg2Module } 
    from './requestPasswordReset/requestPasswordReset.ts';

import { ResearchResourceNg2Module } 
    from './researchResource/researchResource.ts';

import { ResearchResourceDeleteNg2Module } 
    from './researchResource/researchResourceDelete.ts';

import { ResetPasswordNg2Module }
    from './resetPassword/resetPassword.ts';

import { SearchNg2Module } 
    from './search/search.ts';

import { SecurityQuestionEditNg2Module } 
    from './securityQuestionEdit/securityQuestionEdit.ts';

import { SelfServiceNg2Module } 
    from './selfService/selfService.ts';

import { SelfServiceAddContactNg2Module } 
    from './selfServiceAddContact/selfServiceAddContact.ts';

import { SelfServiceExistingSubMemberNg2Module } 
    from './selfServiceExistingSubMember/selfServiceExistingSubMember.ts';

import { SelfServiceRemoveContactNg2Module } 
    from './selfServiceRemoveContact/selfServiceRemoveContact.ts';

import { SelfServiceRemoveSubMemberNg2Module } 
    from './selfServiceRemoveSubMember/selfServiceRemoveSubMember.ts';

import { Social2FANg2Module }
    from './social2FA/social2FA.ts';

import { SwitchUserNg2Module }
    from './switchUser/switchUser.ts';

import { ThanksForRegisteringNg2Module } 
    from './thanksForRegistering/thanksForRegistering.ts';

import { ThanksForVerifyingNg2Module } 
    from './thanksForVerifying/thanksForVerifying.ts';

import { TwoFASetupNg2Module }
    from './2FASetup/twoFASetup.ts';

import { TwoFaStateNg2Module }
    from './2FAState/twoFAState.ts';

import { VerifyEmailNg2Module }
    from './verifyEmail/verifyEmail.ts';

import { WidgetNg2Module } 
    from './widget/widget.ts';

import { WorksBulkDeleteNg2Module } 
    from './works/worksBulkDelete.ts';

import { WorksDeleteNg2Module } 
    from './works/worksDelete.ts';

import { WorksFormNg2Module } 
    from './works/worksForm.ts';

import { WorksNg2Module } 
    from './works/works.ts';

import { SearchService } 
    from '../shared/search.service.ts'; 

import { WorkspaceService } 
    from '../shared/workspace.service.ts'; 

import { WorkSpaceSummaryNg2Module } 
    from './workspaceSummary/workspaceSummary.ts';

import { WorksPrivacyPreferencesNg2Module } 
    from './worksPrivacyPreferences/worksPrivacyPreferences.ts';

import { UnsubscribeNg2Module }
    from './unsubscribe/unsubscribe.ts';

import { DelegatorsNg2Module } 
    from './delegators/delegators.ts';    
    
export class MetaXSRFStrategy implements XSRFStrategy {
    constructor() {
    }

    configureRequest(req: Request): any {
        let token = document.querySelector("meta[name='_csrf']").getAttribute("content");
        let header = document.querySelector("meta[name='_csrf_header']").getAttribute("content");
        if (token && header) {
            req.headers.set(header, token);
        }
    }
}
///////////////////
import {Injectable} 
    from '@angular/core';

import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } 
    from '@angular/common/http';

import { Observable } 
    from 'rxjs';

import { HTTP_INTERCEPTORS, HttpHeaders } from '@angular/common/http';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  constructor() {}
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
      let token = document.querySelector("meta[name='_csrf']").getAttribute("content");
      let header = document.querySelector("meta[name='_csrf_header']").getAttribute("content");
    
    /*request = request.clone({
      setHeaders: {
        header: token
      }
    });
    return next.handle(request);*/
    let _request = request.clone();
    //_request.headers.append(header, token);
    //console.log('headers', header, 'token', token);
    _request.headers.set(header, token);


    let headers2 = new HttpHeaders();
    //console.log('headers2a', headers2);
    headers2 = headers2.append(header, token);
    //console.log('headers2b', headers2);

    //console.log('interceptor', _request, _request.headers, _request.headers.get(header));
    return next.handle(_request);
  }
}
/*
let token = document.querySelector("meta[name='_csrf']").getAttribute("content");
let header = document.querySelector("meta[name='_csrf_header']").getAttribute("content");
if (token && header) {
    req.headers.set(header, token);
}
*/
///////////////////////////////

@Component(
    {
        selector: 'root-cmp',
        template: '<div class="ng-view"></div>'
    }
) 
export class RootCmp {
}

@NgModule({
    bootstrap: [
        RootCmp
    ],
    declarations: [
        RootCmp
    ],
    imports: [
        /* Ng Modules */
        BrowserModule,
        CommonModule, 
        FormsModule,
        HttpClientModule, //angular5
        HttpModule, //Angular2
        JsonpModule,
        NgbModule.forRoot(),
        UpgradeModule,
        /* User Generated Modules */
        AffiliationNg2Module,//Aproved
        AffiliationDeleteNg2Module,//Aproved
        AffiliationFormNg2Module,//Aproved
        AlertBannerNg2Module,
        AllConsortiumContactsNg2Module,
        BiographyNg2Module, //Approved
        ClaimThanksNg2Module,
        ClientEditNg2Module,
        DeactivateAccountNg2Module,
        DeactivateAccountMessageNg2Module,
        DeprecateAccountNg2Module,
        EmailsFormNg2Module,//Aproved
        EmailsNg2Module,//Aproved
        EmailUnverifiedWarningNg2Module,//Aproved
        EmailVerificationSentMesssageNg2Module,//Aproved
        ExternalIdentifiersNg2Module,
        ExtIdPopoverNg2Module,
        HeaderNg2Module,
        FundingDeleteNg2Module,
        FundingFormNg2Module,
        FundingNg2Module,
        HomeNg2Module,
        LanguageNg2Module,
        LinkAccountNg2Module,
        ModalNg2Module, //Approved
        NameNg2Module, //Approved
        NotificationsNg2Module,
        PasswordEditNg2Module,
        PersonNg2Module,
        PersonalInfoNg2Module,
        PublicEduAffiliationNg2Module,
        SecurityQuestionEditNg2Module,
        OauthAuthorizationNg2Module,
        OrgIdentifierPopoverNg2Module,
        ReactivationNg2Module,
        RegisterDuplicatesNg2Module,
        RequestPasswordResetNg2Module,
        ResearchResourceNg2Module,
        ResearchResourceDeleteNg2Module,
        ResetPasswordNg2Module,
        SearchNg2Module, //Approved
        SelfServiceNg2Module, //Approved
        SelfServiceAddContactNg2Module, //Approved
        SelfServiceExistingSubMemberNg2Module, //Approved
        SelfServiceRemoveContactNg2Module, //Approved
        SelfServiceRemoveSubMemberNg2Module, //Approved
        Social2FANg2Module,
        SwitchUserNg2Module,
        ThanksForRegisteringNg2Module,
        ThanksForVerifyingNg2Module,
        TwoFaStateNg2Module,
        TwoFASetupNg2Module,
        UnsubscribeNg2Module,
        VerifyEmailNg2Module,
        WidgetNg2Module, //Approved
        WorksBulkDeleteNg2Module,
        WorksDeleteNg2Module,
        WorksFormNg2Module,
        WorksNg2Module,
        WorkSpaceSummaryNg2Module,
        WorksPrivacyPreferencesNg2Module,
        DelegatorsNg2Module
    ],
    providers: [
        { 
            provide: XSRFStrategy, 
            useClass: MetaXSRFStrategy
        },
        /*{
            provide: HTTP_INTERCEPTORS,
            useClass: TokenInterceptor,
            multi: true
        }*/
    ]

})

export class Ng2AppModule {
    constructor( public upgrade: UpgradeModule ){
        console.log('v0.9.23');
    }
}

