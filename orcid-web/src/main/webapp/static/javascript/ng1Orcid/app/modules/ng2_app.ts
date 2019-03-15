declare var OrcidCookie: any;

//Angular imports

import { CommonModule } 
    from '@angular/common'; 

import { HttpClientModule } 
    from '@angular/common/http';

import { Component, NgModule } 
    from '@angular/core';

import { FormsModule, ReactiveFormsModule } 
    from '@angular/forms';

import { HttpModule, JsonpModule, Request, XSRFStrategy } 
    from '@angular/http';

import { BrowserModule } 
    from "@angular/platform-browser";

import { UpgradeModule } 
    from '@angular/upgrade/static';

import {NgbTypeaheadModule} from '@ng-bootstrap/ng-bootstrap'

//User generated modules imports

import { AccountSettingsNg2Module } 
    from './accountSettings/accountSettings';

import { AdminActionsNg2Module } 
    from './adminActions/adminActions';    

import { AffiliationNg2Module } 
    from './affiliation/affiliation';

import { AffiliationDeleteNg2Module } 
    from './affiliation/affiliationDelete';

import { AffiliationFormNg2Module } 
    from './affiliation/affiliationForm';

import { AlertBannerNg2Module } 
    from './alertBanner/alertBanner';

import { AllConsortiumContactsNg2Module } 
    from './allConsortiumContacts/allConsortiumContacts';
    
import { AuthorizeDelegateResultNg2Module } 
    from './delegators/authorizeDelegateResult';

import { BiographyNg2Module } 
    from './biography/biography';

import { ClaimNg2Module }
    from './claim/claim';

import { ClientEditNg2Module } 
    from './clientEdit/clientEdit'; 

import { DelegatorsNg2Module } 
    from './delegators/delegators';  

import { EmailsNg2Module } 
    from './emails/emails';

import { EmailsFormNg2Module } 
    from './emailsForm/emailsForm';

import { EmailUnverifiedWarningNg2Module } 
    from './emailUnverifiedWarning/emailUnverifiedWarning';

import { EmailVerificationSentMesssageNg2Module } 
    from './emailVerificationSentMessage/emailVerificationSentMessage';

import { ExtIdPopoverNg2Module } 
    from './extIdPopover/extIdPopover';

import { FundingDeleteNg2Module } 
    from './funding/fundingDelete';

import { FundingNg2Module } 
    from './funding/funding';

import { FundingFormNg2Module } 
    from './funding/fundingForm';

import { HeaderNg2Module } 
    from './header/header';

import { HomeNg2Module } 
    from './home/home';

import { LanguageNg2Module }
    from './language/language';

import { LinkAccountNg2Module } 
    from './linkAccount/linkAccount';

import { MembersListNg2Module } 
    from './membersList/membersList';

import { ModalNg2Module }
    from './modalNg2/modal-ng';

import { MyOrcidAlertsNg2Module } 
    from './myOrcidAlerts/myOrcidAlerts';

import { NotificationsNg2Module }
    from './notifications/notifications';

import { NotificationAlertsNg2Module }
    from './notificationAlerts/notificationAlerts';

import { PeerReviewNg2Module } 
    from './peerReview/peerReview';

import { PeerReviewDeleteNg2Module } 
    from './peerReview/peerReviewDelete';

import { PersonNg2Module } 
    from './person/person';

import { PrintRecordNg2Module } 
    from './printRecord/printRecord';

import { PublicRecordNg2Module } 
    from './publicRecord/publicRecord';

import { OauthAuthorizationNg2Module } 
    from './oauthAuthorization/oauthAuthorization';

import { OrgIdentifierPopoverNg2Module } 
    from './orgIdentifierPopover/orgIdentifierPopover';

import { ReactivationNg2Module } 
    from './reactivation/reactivation';

import { RegisterDuplicatesNg2Module } 
    from './registerDuplicates/registerDuplicates';

import { RequestPasswordResetNg2Module } 
    from './requestPasswordReset/requestPasswordReset';

import { ResearchResourceNg2Module } 
    from './researchResource/researchResource';

import { ResearchResourceDeleteNg2Module } 
    from './researchResource/researchResourceDelete';

import { ResendClaimNg2Module }
    from './resendClaim/resendClaim';    

import { ResetPasswordNg2Module }
    from './resetPassword/resetPassword';

import { SearchNg2Module } 
    from './search/search';

import { SelfServiceNg2Module } 
    from './selfService/selfService';

import { SelfServiceAddContactNg2Module } 
    from './selfServiceAddContact/selfServiceAddContact';

import { SelfServiceExistingSubMemberNg2Module } 
    from './selfServiceExistingSubMember/selfServiceExistingSubMember';

import { SelfServiceRemoveContactNg2Module } 
    from './selfServiceRemoveContact/selfServiceRemoveContact';

import { SelfServiceRemoveSubMemberNg2Module } 
    from './selfServiceRemoveSubMember/selfServiceRemoveSubMember';

import { Social2FANg2Module }
    from './social2FA/social2FA';

import { TwoFaSetupNg2Module }
    from './2FASetup/twoFASetup';
    
import { StatisticsNg2Module }
    from './statistics/statistics';

import { UnsubscribeNg2Module }
    from './unsubscribe/unsubscribe';  

import { WidgetNg2Module } 
    from './widget/widget';

import { WorksBulkDeleteNg2Module } 
    from './works/worksBulkDelete';

import { WorksDeleteNg2Module } 
    from './works/worksDelete';

import { WorksFormNg2Module } 
    from './works/worksForm';

import { WorksNg2Module } 
    from './works/works.ts';

import { ManageMembersNg2Module } 
    from './manageMembers/manageMembers';
    
import { RecordCorrectionsNg2Module }
    from './recordCorrections/recordCorrections'
    
import { DeveloperToolsNg2Module } 
    from './developerTools/developerTools';    

import { idBannerNg2Module }  
    from './idBanner/idBanner'

import { qrcodeNg2Module } 
    from './qrcode/qrcode'

import { lastModifiedNg2Module } 
    from './lastModified/lastModified';

import { bioNg2Module }
    from './bio/bio';

import { printIdBannerNg2Module } 
    from './printIdBanner/printIdBanner';
    
import { HtmlHeadNg2Module } 
    from './htmlHead/htmlHead';

import { OauthHeaderNg2Module } 
    from './oauthHeader/oauthHeader';    

import { FooterNg2Module } 
    from './footer/footer';   
    
import { MaintenanceMessageNg2Module } 
    from './maintenanceMessage/maintenanceMessage';    
        
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
       if(request.method == 'GET') {
           return next.handle(request)
       } else {
           // Add CSRF headers for non GET requests
           const newHeaders: {[name: string]: string | string[]; } = {};
           for (const key of request.headers.keys()) {
               newHeaders[key] = request.headers.getAll(key);
           }          
           newHeaders['x-xsrf-token'] = OrcidCookie.getCookie('XSRF-TOKEN');           
           let _request = request.clone({headers: new HttpHeaders(newHeaders)});

           return next.handle(_request);
       }            
   }
}

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
        ReactiveFormsModule,
        HttpClientModule, //angular5
        HttpModule, //Angular2
        JsonpModule,
        NgbTypeaheadModule.forRoot(),
        UpgradeModule,
        /* User Generated Modules */
        AccountSettingsNg2Module,
        AdminActionsNg2Module,
        AffiliationNg2Module,//Aproved
        AffiliationDeleteNg2Module,//Aproved
        AffiliationFormNg2Module,//Aproved
        AlertBannerNg2Module,
        AllConsortiumContactsNg2Module,
        AuthorizeDelegateResultNg2Module,
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
        HomeNg2Module,
        FundingDeleteNg2Module,
        FundingFormNg2Module,
        FundingNg2Module,
        LanguageNg2Module,
        LinkAccountNg2Module,
        MembersListNg2Module, //Approved
        ModalNg2Module, //Approved
        MyOrcidAlertsNg2Module,
        NotificationsNg2Module,
        NotificationAlertsNg2Module,
        PeerReviewNg2Module,
        PeerReviewDeleteNg2Module,
        PersonNg2Module,
        PrintRecordNg2Module,
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
        TwoFaSetupNg2Module,
        StatisticsNg2Module,
        UnsubscribeNg2Module,
        WidgetNg2Module, //Approved
        WorksBulkDeleteNg2Module,
        WorksDeleteNg2Module,
        WorksFormNg2Module,
        WorksNg2Module,
        ManageMembersNg2Module,
        DeveloperToolsNg2Module,
        RecordCorrectionsNg2Module,
        AdminActionsNg2Module,
        RecordCorrectionsNg2Module,
        idBannerNg2Module,
        qrcodeNg2Module,
        lastModifiedNg2Module, 
        bioNg2Module,
        printIdBannerNg2Module,
        HtmlHeadNg2Module,
        OauthHeaderNg2Module,
        FooterNg2Module,
        MaintenanceMessageNg2Module
    ],
    providers: [{ 
            provide: HTTP_INTERCEPTORS,
            useClass: TokenInterceptor,
            multi: true
        }
    ]

})

export class Ng2AppModule {
    constructor( public upgrade: UpgradeModule ){
        console.log('v0.9.23');
    }
}