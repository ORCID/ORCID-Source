/**
 * This file defines the root module of the Angular 1 of the application.
 */

//Angular and other libraries imports
import * as angular from 'angular'
import * as ngCookies from 'angular-cookies'
import * as ngSanitize from 'angular-sanitize'
import * as uibootstraptypeahead from 'angular-ui-bootstrap'
import * as vcRecaptcha from 'angular-recaptcha'
import 'angular-route'

//User generated imports

import { AccountSettingsModule } 
    from './accountSettings/accountSettings';

import { AdminActionsModule } 
    from './adminActions/adminActions';  

import { AffiliationModule } 
    from './affiliation/affiliation';

import { AffiliationDeleteModule } 
    from './affiliation/affiliationDelete';

import { AffiliationFormModule } 
    from './affiliation/affiliationForm';

import { AlertBannerModule } 
    from './alertBanner/alertBanner';

import { AllConsortiumContactsModule } 
    from './allConsortiumContacts/allConsortiumContacts';
    
import { AuthorizeDelegateResultModule } 
    from './delegators/authorizeDelegateResult';

import { BiographyModule } 
    from './biography/biography';

import {ClaimModule}
    from './claim/claim';

import { ClientEditModule } 
    from './clientEdit/clientEdit';

import { DelegatorsModule } 
    from './delegators/delegators';

import { DeveloperToolsModule } 
    from './developerTools/developerTools';  

import { EmailsModule } 
    from './emails/emails';

import { EmailsFormModule } 
    from './emailsForm/emailsForm';

import { EmailUnverifiedWarningModule } 
    from './emailUnverifiedWarning/emailUnverifiedWarning';

import { EmailVerificationSentMesssageModule } 
    from './emailVerificationSentMessage/emailVerificationSentMessage';

import { ExtIdPopoverModule } 
    from './extIdPopover/extIdPopover';

import { FundingDeleteModule } 
    from './funding/fundingDelete';

import { FundingModule } 
    from './funding/funding';

import { FundingFormModule } 
    from './funding/fundingForm';

import { HeaderModule } 
    from './header/header';

import { Header2Module } 
    from './header2/header2';


import { HomeModule } 
    from './home/home';

import { LanguageModule } 
    from './language/language';

import { LinkAccountModule } 
    from './linkAccount/linkAccount';

import { MembersListModule } 
    from './membersList/membersList';

import { ModalModule } 
    from './modalNg2/modal-ng';

import { MyOrcidAlertsModule } 
    from './myOrcidAlerts/myOrcidAlerts';

import { NotificationsModule }
    from './notifications/notifications';

import { NotificationAlertsModule }
    from './notificationAlerts/notificationAlerts';

import { OauthAuthorizationModule } 
    from './oauthAuthorization/oauthAuthorization';

import { OrgIdentifierPopoverModule } 
    from './orgIdentifierPopover/orgIdentifierPopover';

import { PeerReviewModule } 
    from './peerReview/peerReview';

import { PeerReviewDeleteModule } 
    from './peerReview/peerReviewDelete';

import { PersonModule } 
    from './person/person';

import { PrintRecordModule } 
    from './printRecord/printRecord';

import { PublicRecordModule } 
    from './publicRecord/publicRecord';

import { ReactivationModule } 
    from './reactivation/reactivation';
    
import { RecordCorrectionsModule }
    from './recordCorrections/recordCorrections';

import { RegisterDuplicatesModule } 
    from './registerDuplicates/registerDuplicates';

import { RequestPasswordResetModule } 
    from './requestPasswordReset/requestPasswordReset';

import { ResetPasswordModule }
    from './resetPassword/resetPassword';

import { ResearchResourceModule } 
    from './researchResource/researchResource';

import { ResearchResourceDeleteModule } 
    from './researchResource/researchResourceDelete';

import { ResendClaimModule } 
    from './resendClaim/resendClaim';

import { SearchModule } 
    from './search/search';

import { SelfServiceModule } 
    from './selfService/selfService';

import { SelfServiceAddContactModule } 
    from './selfServiceAddContact/selfServiceAddContact';

import { SelfServiceExistingSubMemberModule } 
    from './selfServiceExistingSubMember/selfServiceExistingSubMember';

import { SelfServiceRemoveContactModule } 
    from './selfServiceRemoveContact/selfServiceRemoveContact';

import { SelfServiceRemoveSubMemberModule } 
    from './selfServiceRemoveSubMember/selfServiceRemoveSubMember';

import { Social2FAModule }
    from './social2FA/social2FA';

import { TwoFaSetupModule } 
    from './2FASetup/twoFASetup';

import { StatisticsModule } 
    from './statistics/statistics';

import { UnsubscribeModule }
    from './unsubscribe/unsubscribe';

import { WidgetModule } 
    from './widget/widget';

import { WorksBulkDeleteModule } 
    from './works/worksBulkDelete';

import { WorksDeleteModule } 
    from './works/worksDelete';

import { WorksFormModule } 
    from './works/worksForm';

import { WorksModule } 
    from './works/works';

import { ManageMembersModule } 
    from './manageMembers/manageMembers';      
    
import { idBannerModule } 
    from './idBanner/idBanner'

import { qrcodeModule } 
    from './qrcode/qrcode';

import { lastModifiedModule }
    from './lastModified/lastModified'

import { bioModule } 
    from './bio/bio';

import { printIdBannerModule } 
    from './printIdBanner/printIdBanner';

import { HtmlHeadModule } 
    from './htmlHead/htmlHead';    

import { OauthHeaderModule } 
    from './oauthHeader/oauthHeader';

import { FooterModule } 
    from './footer/footer';    

import { MaintenanceMessageModule } 
    from './maintenanceMessage/maintenanceMessage';
    
export const orcidApp = angular.module(
    'orcidApp', 
    [
        ngCookies,
        ngSanitize, 
        vcRecaptcha,
        uibootstraptypeahead,
        AccountSettingsModule.name,
        AdminActionsModule.name,
        AffiliationModule.name,
        AffiliationDeleteModule.name,
        AffiliationFormModule.name,
        AlertBannerModule.name,
        AllConsortiumContactsModule.name,
        AuthorizeDelegateResultModule.name,
        BiographyModule.name,
        ClaimModule.name, 
        ClientEditModule.name,
        DelegatorsModule.name,
        DeveloperToolsModule.name,
        EmailsFormModule.name,
        EmailsModule.name,
        EmailUnverifiedWarningModule.name,
        EmailVerificationSentMesssageModule.name,
        ExtIdPopoverModule.name,
        FundingDeleteModule.name,
        FundingFormModule.name,
        FundingModule.name,
        HeaderModule.name,
        Header2Module.name,
        HomeModule.name,
        LanguageModule.name,
        LinkAccountModule.name,
        MembersListModule.name,
        ModalModule.name,
        MyOrcidAlertsModule.name,
        NotificationsModule.name,
        NotificationAlertsModule.name,
        OauthAuthorizationModule.name,
        OrgIdentifierPopoverModule.name,
        PeerReviewModule.name,
        PeerReviewDeleteModule.name,
        PersonModule.name,
        PrintRecordModule.name,
        PublicRecordModule.name,
        ReactivationModule.name,
        RecordCorrectionsModule.name,
        RegisterDuplicatesModule.name,
        RequestPasswordResetModule.name,
        ResetPasswordModule.name,
        ResearchResourceModule.name,
        ResearchResourceDeleteModule.name,
        ResendClaimModule.name,
        SearchModule.name,
        SelfServiceModule.name,
        SelfServiceAddContactModule.name,
        SelfServiceExistingSubMemberModule.name,
        SelfServiceRemoveContactModule.name,
        SelfServiceRemoveSubMemberModule.name,
        Social2FAModule.name,
        TwoFaSetupModule.name,
        StatisticsModule.name,
        UnsubscribeModule.name,
        WidgetModule.name,
        WorksBulkDeleteModule.name,
        WorksDeleteModule.name,
        WorksFormModule.name,
        WorksModule.name,
        ManageMembersModule.name,
        idBannerModule.name,
        qrcodeModule.name,
        lastModifiedModule.name,
        bioModule.name,
        printIdBannerModule.name,
        HtmlHeadModule.name,
        OauthHeaderModule.name,
        FooterModule.name,
        MaintenanceMessageModule.name
    ]
);
