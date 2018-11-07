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

import { AccountSettingsNg2Module } 
    from './accountSettings/accountSettings.ts';

import { AdminActionsNg2Module } 
    from './adminActions/adminActions.ts';    

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

import { ClaimNg2Module }
    from './claim/claim.ts';

import { ClientEditNg2Module } 
    from './clientEdit/clientEdit.ts'; 

import { DelegatorsNg2Module } 
    from './delegators/delegators.ts';  

import { EmailsNg2Module } 
    from './emails/emails.ts';

import { EmailsFormNg2Module } 
    from './emailsForm/emailsForm.ts';

import { EmailUnverifiedWarningNg2Module } 
    from './emailUnverifiedWarning/emailUnverifiedWarning.ts';

import { EmailVerificationSentMesssageNg2Module } 
    from './emailVerificationSentMessage/emailVerificationSentMessage.ts';

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

import { MembersListNg2Module } 
    from './membersList/membersList.ts';

import { ModalNg2Module }
    from './modalNg2/modal-ng.ts';

import { MyOrcidAlertsNg2Module } 
    from './myOrcidAlerts/myOrcidAlerts.ts';

import { NameNg2Module } 
    from './name/name.ts';

import { NotificationsNg2Module }
    from './notifications/notifications.ts';

import { NotificationAlertsNg2Module }
    from './notificationAlerts/notificationAlerts.ts';

import { PeerReviewNg2Module } 
    from './peerReview/peerReview.ts';

import { PeerReviewDeleteNg2Module } 
    from './peerReview/peerReviewDelete.ts';

import { PersonNg2Module } 
    from './person/person.ts';

import { PrintRecordNg2Module } 
    from './printRecord/printRecord.ts';

import { PublicEduAffiliationNg2Module }
    from './publicEduAffiliation/publicEduAffiliation.ts';

import { PublicRecordNg2Module } 
    from './publicRecord/publicRecord.ts';

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

import { ResendClaimNg2Module }
    from './resendClaim/resendClaim.ts';    

import { ResetPasswordNg2Module }
    from './resetPassword/resetPassword.ts';

import { SearchNg2Module } 
    from './search/search.ts';

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

import { TwoFaSetupNg2Module }
    from './2FASetup/twoFASetup.ts';

import { UnsubscribeNg2Module }
    from './unsubscribe/unsubscribe.ts';  

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
    
import { WorksMergeChoosePreferredVersionNg2Module } 
    from './works/worksMergeChoosePreferredVersion.ts';
    
import { WorksMergeSuggestionsNg2Module } 
    from './works/worksMergeSuggestions.ts';
    
import { RecordCorrectionsNg2Module }
    from './recordCorrections/recordCorrections.ts'
    
import { DeveloperToolsNg2Module } 
    from './developerTools/developerTools.ts';    
    
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
        AccountSettingsNg2Module,
        AdminActionsNg2Module,
        AffiliationNg2Module,//Aproved
        AffiliationDeleteNg2Module,//Aproved
        AffiliationFormNg2Module,//Aproved
        AlertBannerNg2Module,
        AllConsortiumContactsNg2Module,
        BiographyNg2Module, //Approved
        ClaimNg2Module,
        ClientEditNg2Module,
        DelegatorsNg2Module,
        EmailsFormNg2Module,//Aproved
        EmailsNg2Module,//Aproved
        EmailUnverifiedWarningNg2Module,//Aproved
        EmailVerificationSentMesssageNg2Module,//Aproved
        ExtIdPopoverNg2Module,
        HeaderNg2Module,
        FundingDeleteNg2Module,
        FundingFormNg2Module,
        FundingNg2Module,
        HomeNg2Module,
        LanguageNg2Module,
        LinkAccountNg2Module,
        MembersListNg2Module, //Approved
        ModalNg2Module, //Approved
        MyOrcidAlertsNg2Module,
        NameNg2Module, //Approved
        NotificationsNg2Module,
        NotificationAlertsNg2Module,
        PeerReviewNg2Module,
        PeerReviewDeleteNg2Module,
        PersonNg2Module,
        PrintRecordNg2Module,
        PublicEduAffiliationNg2Module,
        PublicRecordNg2Module,
        OauthAuthorizationNg2Module,
        OrgIdentifierPopoverNg2Module,
        ReactivationNg2Module,
        RegisterDuplicatesNg2Module,
        RequestPasswordResetNg2Module,
        ResearchResourceNg2Module,
        ResearchResourceDeleteNg2Module,
        ResendClaimNg2Module,
        ResetPasswordNg2Module,
        SearchNg2Module, //Approved
        SelfServiceNg2Module, //Approved
        SelfServiceAddContactNg2Module, //Approved
        SelfServiceExistingSubMemberNg2Module, //Approved
        SelfServiceRemoveContactNg2Module, //Approved
        SelfServiceRemoveSubMemberNg2Module, //Approved
        Social2FANg2Module,
        SwitchUserNg2Module,
        TwoFaSetupNg2Module,
        UnsubscribeNg2Module,
        WidgetNg2Module, //Approved
        WorksBulkDeleteNg2Module,
        WorksDeleteNg2Module,
        WorksFormNg2Module,
        WorksNg2Module,
        WorksMergeChoosePreferredVersionNg2Module,
        WorksMergeSuggestionsNg2Module,
        DeveloperToolsNg2Module,
        AdminActionsNg2Module,
        RecordCorrectionsNg2Module
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

