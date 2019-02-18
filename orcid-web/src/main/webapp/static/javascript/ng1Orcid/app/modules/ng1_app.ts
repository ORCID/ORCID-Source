/**
 * This file defines the root module of the Angular 1 of the application.
 */

//Angular and other libraries imports
import * as $ from 'jquery'
import * as angular from 'angular'
import * as ngCookies from 'angular-cookies'
import * as ngSanitize from 'angular-sanitize'
import * as uibootstraptypeahead from 'angular-ui-bootstrap'
import * as vcRecaptcha from 'angular-recaptcha'
import 'angular-route'

//User generated imports

import { AccountSettingsModule } 
    from './accountSettings/accountSettings.ts';

import { AdminActionsModule } 
    from './adminActions/adminActions.ts';  

import { AffiliationModule } 
    from './affiliation/affiliation.ts';

import { AffiliationDeleteModule } 
    from './affiliation/affiliationDelete.ts';

import { AffiliationFormModule } 
    from './affiliation/affiliationForm.ts';

import { AlertBannerModule } 
    from './alertBanner/alertBanner.ts';

import { AllConsortiumContactsModule } 
    from './allConsortiumContacts/allConsortiumContacts.ts';

import { BiographyModule } 
    from './biography/biography.ts';

import {ClaimModule}
    from './claim/claim.ts';

import { ClientEditModule } 
    from './clientEdit/clientEdit.ts';

import { DelegatorsModule } 
    from './delegators/delegators.ts';

import { DeveloperToolsModule } 
    from './developerTools/developerTools.ts';  

import { EmailsModule } 
    from './emails/emails.ts';

import { EmailsFormModule } 
    from './emailsForm/emailsForm.ts';

import { EmailUnverifiedWarningModule } 
    from './emailUnverifiedWarning/emailUnverifiedWarning.ts';

import { EmailVerificationSentMesssageModule } 
    from './emailVerificationSentMessage/emailVerificationSentMessage.ts';

import { ExtIdPopoverModule } 
    from './extIdPopover/extIdPopover.ts';

import { FundingDeleteModule } 
    from './funding/fundingDelete.ts';

import { FundingModule } 
    from './funding/funding.ts';

import { FundingFormModule } 
    from './funding/fundingForm.ts';

import { HeaderModule } 
    from './header/header.ts';

import { HomeModule } 
    from './home/home.ts';

import { LanguageModule } 
    from './language/language.ts';

import { LinkAccountModule } 
    from './linkAccount/linkAccount.ts';

import { MembersListModule } 
    from './membersList/membersList.ts';

import { ModalModule } 
    from './modalNg2/modal-ng.ts';

import { MyOrcidAlertsModule } 
    from './myOrcidAlerts/myOrcidAlerts.ts';

import { NotificationsModule }
    from './notifications/notifications.ts';

import { NotificationAlertsModule }
    from './notificationAlerts/notificationAlerts.ts';

import { OauthAuthorizationModule } 
    from './oauthAuthorization/oauthAuthorization.ts';

import { OrgIdentifierPopoverModule } 
    from './orgIdentifierPopover/orgIdentifierPopover.ts';

import { PeerReviewModule } 
    from './peerReview/peerReview.ts';

import { PeerReviewDeleteModule } 
    from './peerReview/peerReviewDelete.ts';

import { PersonModule } 
    from './person/person.ts';

import { PrintRecordModule } 
    from './printRecord/printRecord.ts';

import { PublicEduAffiliationModule }
    from './publicEduAffiliation/publicEduAffiliation.ts';

import { PublicRecordModule } 
    from './publicRecord/publicRecord.ts';

import { ReactivationModule } 
    from './reactivation/reactivation.ts';
    
import { RecordCorrectionsModule }
    from './recordCorrections/recordCorrections.ts';

import { RegisterDuplicatesModule } 
    from './registerDuplicates/registerDuplicates.ts';

import { RequestPasswordResetModule } 
    from './requestPasswordReset/requestPasswordReset.ts';

import { ResetPasswordModule }
    from './resetPassword/resetPassword.ts';

import { ResearchResourceModule } 
    from './researchResource/researchResource.ts';

import { ResearchResourceDeleteModule } 
    from './researchResource/researchResourceDelete.ts';

import { ResendClaimModule } 
    from './resendClaim/resendClaim.ts';

import { SearchModule } 
    from './search/search.ts';

import { SelfServiceModule } 
    from './selfService/selfService.ts';

import { SelfServiceAddContactModule } 
    from './selfServiceAddContact/selfServiceAddContact.ts';

import { SelfServiceExistingSubMemberModule } 
    from './selfServiceExistingSubMember/selfServiceExistingSubMember.ts';

import { SelfServiceRemoveContactModule } 
    from './selfServiceRemoveContact/selfServiceRemoveContact.ts';

import { SelfServiceRemoveSubMemberModule } 
    from './selfServiceRemoveSubMember/selfServiceRemoveSubMember.ts';

import { Social2FAModule }
    from './social2FA/social2FA.ts';

import { TwoFaSetupModule } 
    from './2FASetup/twoFASetup.ts';

import { UnsubscribeModule }
    from './unsubscribe/unsubscribe.ts';

import { WidgetModule } 
    from './widget/widget.ts';

import { WorksBulkDeleteModule } 
    from './works/worksBulkDelete.ts';

import { WorksDeleteModule } 
    from './works/worksDelete.ts';

import { WorksFormModule } 
    from './works/worksForm.ts';

import { WorksModule } 
    from './works/works.ts';
    
import { WorksMergeSuggestionsModule } 
    from './works/worksMergeSuggestions.ts';

import { ManageMembersModule } 
    from './manageMembers/manageMembers.ts';      
    
import { idBannerModule } 
    from './idBanner/idBanner.ts'

import { qrcodeModule } 
    from './qrcode/qrcode.ts';

import { lastModifiedModule }
    from './lastModified/lastModified.ts'

import { bioModule } 
    from './bio/bio.ts';

import { printIdBannerModule } 
    from './printIdBanner/printIdBanner.ts';

import { HtmlHeadModule } 
    from './htmlHead/htmlHead.ts';    

import { ScriptsModule } 
    from './scripts/scripts.ts';

import { OauthHeaderModule } 
    from './oauthHeader/oauthHeader.ts';
    
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
        PublicEduAffiliationModule.name,
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
        UnsubscribeModule.name,
        WidgetModule.name,
        WorksBulkDeleteModule.name,
        WorksDeleteModule.name,
        WorksFormModule.name,
        WorksModule.name,
        WorksMergeSuggestionsModule.name,
        ManageMembersModule.name,
        idBannerModule.name,
        qrcodeModule.name,
        lastModifiedModule.name,
        bioModule.name,
        printIdBannerModule.name,
        HtmlHeadModule.name,
        ScriptsModule.name,
        OauthHeaderModule.name
    ]
);
